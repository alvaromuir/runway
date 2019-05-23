package com.verizon.itanalytics.dataengineering.runway.microservice

import JsonProtocol._
import MicroService.system
import Tables._
import utils.Utils
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ExceptionHandler, Route}

import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Flow}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.{ByteString, Timeout}
import com.typesafe.config.{Config, ConfigFactory}

import org.dmg.pmml
import org.dmg.pmml.FieldName
import org.jpmml.evaluator.ModelEvaluator
import slick.jdbc.H2Profile.api._
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 29, 2018
 */

trait ServiceRoutes extends Utils {

  import akka.http.scaladsl.server.Directives._

  private val config: Config = ConfigFactory.load()
  private val apiVersion: String = config.getString("http.apiVersion")
  private val dataUploadPath: String = config.getString("http.dataUploadPath")
  private val timeOut: Int = config.getInt("http.timeOut")

  private implicit val timeout: Timeout = Timeout(timeOut.seconds)

  implicit def routesExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case NonFatal(e) =>
        val errMsg = s"Exception $e at\n${e.getStackTrace}"
        log.error(errMsg)
        complete(jsonize(e))
    }


  def serviceRoutes: Route = {
    import akka.http.scaladsl.server.Directives._

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    pathPrefix("api") {
      handleExceptions(routesExceptionHandler) {
        pathPrefix(s"$apiVersion") {
          path("test") {
            get {
              val resp = "Just a test; Is it tho? \uD83E\uDD14"
              complete(jsonize(resp))
            }
          } ~
          pathPrefix("models") {
            extractRequestContext { ctx =>
              implicit val materializer: Materializer = ctx.materializer
              pathEnd {
                concat(
                  post {
                    toStrictEntity(timeOut seconds) { // this kills streams let' revisit
                      formFields("name",
                                 "project".?,
                                 "description".?,
                                 "author".?) {
                        (name, project, description, author) =>
                          fileUpload("file") {
                            case (metadata, byteSource) =>
                              val sink = FileIO.toPath(Paths
                                .get(dataUploadPath) resolve metadata.fileName)
                              val uploaded = byteSource.runWith(sink)

                              onComplete(uploaded) {
                                case Success(uploadedFile) =>
                                  val filePath =
                                    s"$dataUploadPath/${metadata.fileName}"
                                  val pMML = readPMML(new File(filePath))
                                  val evaluator = evaluatePmml(pMML)

                                  val infoMsg =
                                    s"received ${uploadedFile.count} bytes of '${metadata.fileName}'"
                                  log.info(infoMsg)




                                  val slugifiedName = db.run((for {
                                    m <- models
                                    if m.name === Slugify(name)
                                  } yield m).exists.result).onSuccess {
                                    case false => Slugify(name)
                                    case true => Versionize(Slugify(name))
                                  }

                                  println(slugifiedName)



                                  onComplete(
                                    db.run(
                                      models += Model(
                                        name = Slugify(name),
                                        project = project,
                                        description = description,
                                        algorithm =
                                          Option(evaluator.getSummary),
                                        author = author,
                                        filePath = filePath
                                      ))) {
                                    case Success(_) =>
                                      val infoMsg =
                                        s"model: $name record created as '${Slugify(name)}'"
                                      log.info(infoMsg)
                                      complete(jsonize(
                                        infoMsg,
                                        Option(StatusCodes.Created.intValue)))
                                    case Failure(e) =>
                                      val errMsg =
                                        s"ERROR submitting model: $e"
                                      log.error(errMsg)
                                      complete(jsonize(e))
                                  }
                                case Failure(e) =>
                                  val errMsg = s"ERROR uploading model: $e"
                                  log.error(errMsg)
                                  complete(jsonize(e))
                              }
                          }
                      }
                    }
                  } ~
                  get {
                    onComplete(db.run(models.result)) {
                      case Success(rows) =>
                        complete(jsonize(rows))
                      case Failure(e) =>
                        val errMsg = s"ERROR listing all models: $e"
                        log.error(errMsg)
                        complete(jsonize(e))
                    }
                  }
                )
              } ~
              path(Segments) { segments =>
                val name = segments.head
                segments.size match {
                  case 2 =>
                    val action = segments(1)
                    action match {
                      case "batch" =>
                        toStrictEntity(2 seconds) {
                          post {
                            val maybeModel: Future[Model] =
                              db.run(
                                models.filter(_.name === name).result.head)
                            val maybeEvaluator
                              : Future[ModelEvaluator[_ <: pmml.Model]] =
                              maybeModel.map { model =>
                                evaluatePmml(
                                  readPMML(new File(model.filePath)))
                              }
                            formFields("fields".?) { fields =>
                              fileUpload("csv") {
                                case (metadata, byteSource) =>
                                  val sink = FileIO.toPath(Paths
                                    .get(dataUploadPath) resolve metadata.fileName)

                                  val uploaded = byteSource.runWith(sink)

                                  onComplete(uploaded) {
                                    case Success(_) =>
                                      onComplete(maybeEvaluator) {
                                        case Success(evaluator) =>
                                          // todo: change this to a runnable graph
                                          // todo: check for correct headers
                                          // todo: better error checking for batch
                                          // todo: assign parallelism to settings file
                                          // todo: check for duplicate name slugs and file names
                                          val pmmlSchema =
                                            parsePmml(evaluator.getPMML)

                                          val source = FileIO
                                            .fromPath(Paths.get(
                                              s"$dataUploadPath/${metadata.fileName}"))
                                            .via(CsvParsing.lineScanner())

                                          val estimator =
                                            Flow[Map[String, String]]
                                              .map(_.map {
                                                case (k, v) =>
                                                  k.asInstanceOf[Any] -> v
                                                    .asInstanceOf[Any]
                                              })
                                              .mapAsync(2)(observations => {
                                                Future(createArguments(
                                                  pmmlSchema,
                                                  observations))
                                              })
                                              .mapAsync[util.Map[FieldName,
                                                                 _]](2)(
                                                arguments => {
                                                  Future(evaluator.evaluate(
                                                    arguments))
                                                })
                                              .mapAsync(2)(line => {
                                                Future(s"$line\n")
                                              })
                                              .map(ByteString(_))

                                          val rslts = fields match {
                                            case Some(f) =>
                                              source
                                                .via(CsvToMap
                                                  .withHeadersAsStrings(
                                                    StandardCharsets.UTF_8,
                                                    f.split(",").toSeq: _*))
                                                .via(estimator)
                                            case None =>
                                              source
                                                .via(
                                                  CsvToMap.toMapAsStrings(
                                                    StandardCharsets.UTF_8))
                                                .via(estimator)

                                          }

                                          complete(HttpResponse(entity =
                                            HttpEntity.Chunked.fromData(
                                              ContentTypes.`text/plain(UTF-8)`,
                                              rslts)))

                                      }
                                    case Failure(e) =>
                                      val errMsg =
                                        s"ERROR uploading batch observations: $e"
                                      log.error(errMsg)
                                      complete(jsonize(e))
                                  }
                              }
                            }
                          }
                        }
                      case "details" =>
                        get {
                          onComplete(
                            db.run(models.filter(_.name === name).result)) {
                            case Success(rslts) => // todo: check if results has a length
                              val maybePmml = Future(parsePmml(
                                readPMML(new File(rslts.head.filePath))))
                              onComplete(maybePmml) {
                                case Success(pMML) =>
                                  complete(jsonize(pMML))
                                case Failure(e) =>
                                  val errMsg =
                                    s"ERROR retrieving the model source: $e"
                                  log.error(errMsg)
                                  complete(jsonize(e))
                              }
                            case Failure(e) =>
                              val errMsg = s"ERROR querying the model: $e"
                              log.error(errMsg)
                              complete(jsonize(e))
                          }
                        }
                    }
                  case 1 =>
                    concat(
                      post {
                        val maybeModel: Future[Model] =
                          db.run(models.filter(_.name === name).result.head)
                        val maybeEvaluator
                          : Future[ModelEvaluator[_ <: pmml.Model]] =
                          maybeModel.map { model =>
                            evaluatePmml(readPMML(new File(model.filePath)))
                          }
                        entity(as[JsValue]) {
                          observations =>
                            onComplete(maybeEvaluator) {
                              case Success(evaluator) =>
                                val arguments =
                                  Future(
                                    createArguments(
                                      parsePmml(evaluator.getPMML),
                                      observations
                                        .convertTo[Map[Any, Any]]
                                        .filterKeys(
                                          evaluator.getInputFields.asScala
                                            .map {
                                              _.getName.getValue
                                            }
                                            .toSet[Any])))
                                onComplete(arguments) {
                                  case Success(args) =>
                                    val rslts =
                                      Future(evaluator.evaluate(args))
                                    onComplete(rslts) {
                                      case Success(score) =>
                                        complete(jsonize(score))
                                      case Failure(e) =>
                                        val errMsg =
                                          s"ERROR scoring observation: $e"
                                        log.error(errMsg)
                                        complete(jsonize(e))
                                    }
                                  case Failure(e) =>
                                    val errMsg =
                                      s"ERROR parsing observations: $e"
                                    log.error(errMsg)
                                    complete(jsonize(e))
                                }
                              case Failure(e) =>
                                val errMsg =
                                  s"ERROR initializing evaluator: $e"
                                log.error(errMsg)
                                complete(jsonize(e))
                            }
                        }
                      } ~
                        get {
                          onComplete(
                            db.run(models.filter(_.name === name).result)) {
                            case Success(rslts) =>
                              complete(jsonize(rslts))
                            case Failure(e) =>
                              val errMsg = s"ERROR uploading model: $e"
                              log.error(errMsg)
                              complete(jsonize(e))
                          }
                        } ~
                        put {
                          toStrictEntity(timeOut seconds) { // this kills streams let' revisit
                            formFields("project".?,
                                       "description".?,
                                       "author".?,
                                       "file".?) {
                              (project, description, author, file) =>
                                {
                                  onComplete(db.run(models
                                    .filter(_.name === name)
                                    .result)) {
                                    case Success(rslts) =>
                                      val updatedProject = project match {
                                        case None => rslts.head.project
                                        case _    => project
                                      }
                                      val updatedDescription =
                                        description match {
                                          case None =>
                                            rslts.head.description
                                          case _ => description
                                        }
                                      val updatedAuthor = author match {
                                        case None => rslts.head.author
                                        case _    => author
                                      }
                                      file match {
                                        case None =>
                                          onComplete(
                                            db.run(models
                                              .filter(_.name === name)
                                              .map(u =>
                                                (u.project,
                                                 u.description,
                                                 u.author,
                                                 u.updated_dt))
                                              .update(
                                                (updatedProject,
                                                 updatedDescription,
                                                 updatedAuthor,
                                                 Some(new SimpleDateFormat(
                                                   "MM/dd/yyyy HH:mm:ss z")
                                                   .format(new Date)))))) {
                                            case Success(_) =>
                                              val infoMsg =
                                                s"model: ${Slugify(name)} record updated"
                                              log.info(infoMsg)
                                              complete(jsonize(
                                                infoMsg,
                                                Option(
                                                  StatusCodes.OK.intValue)))

                                            case Failure(e) =>
                                              val errMsg =
                                                s"ERROR updating model: $e"
                                              log.error(errMsg)
                                              complete(jsonize(e))
                                          }
                                        case _ =>
                                          fileUpload("file") {
                                            case (metadata, byteSource) =>
                                              val sink = FileIO.toPath(Paths
                                                .get(dataUploadPath) resolve metadata.fileName)
                                              val uploaded =
                                                byteSource.runWith(sink)

                                              onComplete(uploaded) {
                                                case Success(
                                                    uploadedFile) =>
                                                  val filePath =
                                                    s"$dataUploadPath/${metadata.fileName}"
                                                  val pMML = readPMML(
                                                    new File(filePath))
                                                  val evaluator =
                                                    evaluatePmml(pMML)

                                                  val infoMsg =
                                                    s"received ${uploadedFile.count} bytes of '${metadata.fileName}'"
                                                  log.info(infoMsg)

                                                  onComplete(
                                                    db.run(
                                                      models
                                                        .filter(
                                                          _.name === name)
                                                        .map(u =>
                                                          (u.project,
                                                           u.description,
                                                           u.algorithm,
                                                           u.author,
                                                           u.filePath,
                                                           u.updated_dt))
                                                        .update(
                                                          updatedProject,
                                                          updatedDescription,
                                                          Option(evaluator.getSummary),
                                                          updatedAuthor,
                                                          filePath,
                                                          Some(new SimpleDateFormat(
                                                            "MM/dd/yyyy HH:mm:ss z")
                                                            .format(
                                                              new Date))
                                                        ))) {
                                                    case Success(_) =>
                                                      val infoMsg =
                                                        s"model: ${Slugify(name)} record updated"
                                                      log.info(infoMsg)
                                                      complete(jsonize(
                                                        infoMsg,
                                                        Option(
                                                          StatusCodes.OK.intValue)))
                                                    case Failure(e) =>
                                                      val errMsg =
                                                        s"ERROR updating model: $e"
                                                      log.error(errMsg)
                                                      complete(jsonize(e))
                                                  }
                                                case Failure(e) =>
                                                  val errMsg =
                                                    s"ERROR uploading model: $e"
                                                  log.error(errMsg)
                                                  complete(jsonize(e))
                                              }
                                          }
                                      }
                                    case Failure(e) =>
                                      val errMsg =
                                        s"ERROR updating model ${Slugify(
                                          name)}: $e"
                                      log.error(errMsg)
                                      complete(jsonize(e))
                                  }
                                }
                            }
                          }
                        } ~
                        delete {
                          onComplete(
                            db.run(models.filter(_.name === name).delete)) {
                            case Success(n) =>
                              n match {
                                case x if x < 1 =>
                                  complete(jsonize(
                                    s"ERROR: model ${Slugify(name)} not found",
                                    Option(StatusCodes.NotFound.intValue)))
                                case _ =>
                                  complete(jsonize(
                                    s"model: ${Slugify(name)} record deleted"))
                              }
                            case Failure(e) =>
                              val errMsg = s"ERROR uploading model: $e"
                              log.error(errMsg)
                              complete(jsonize(e))
                          }
                        }
                    )
                }
              }
            }
          }
        }
      }
    }
  }
}
