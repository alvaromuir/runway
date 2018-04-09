package com.verizon.itanalytics.dataengineering.runway.microservice

import java.io.File
import java.nio.file.Paths
import java.util.logging.Logger

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpResponse,
  StatusCodes
}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.pattern.ask
import akka.stream._
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Flow, Framing}
import akka.util.{ByteString, Timeout}
import com.typesafe.config.{Config, ConfigFactory}
import org.jpmml.evaluator.Evaluator
import spray.json._

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent._
import com.verizon.itanalytics.dataengineering.runway.evaluator.Manager.{
  getEvaluator,
  readPMML
}
import com.verizon.itanalytics.dataengineering.runway.microservice.ModelRegistryActor._
import com.verizon.itanalytics.dataengineering.runway.microservice.utils._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import ExecutionContext.Implicits.global
import scala.util.control.NonFatal

trait ModelRoutes extends JsonSupport {
  implicit def system: ActorSystem

  private val config: Config = ConfigFactory.load()
  private val dataUploadDir: String = config.getString("http.dataUploadDir")
  private val timeOut: Int = config.getInt("http.timeOut")
  private val dataLineLimit: Int = config.getInt("http.dataLineLimit")

  private implicit val timeout: Timeout = Timeout(timeOut seconds)
  private val log = Logger.getLogger(this.getClass.getName)

  def modelRegistryActor: ActorRef
  final case class ActionPerformed(description: String)

  val readLine: Flow[ByteString, String, NotUsed] = Flow[ByteString]
    .via(
      Framing.delimiter(ByteString("\n"),
                        maximumFrameLength = dataLineLimit,
                        allowTruncation = true))
    .map(_.utf8String)

  implicit def routesExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case NonFatal(e) =>
        log.info(s"Exception $e at\n${e.getStackTrace}")
        complete(
          HttpResponse(StatusCodes.InternalServerError,
                       entity = s"""{"error":"${e.getMessage}"}"""))
    }

  lazy val modelRoutes: Route =
    pathPrefix("models") {
      extractRequestContext { ctx =>
        implicit val materializer: Materializer = ctx.materializer
        pathEnd {
            concat(
              get {
                val models = (modelRegistryActor ? GetModels).mapTo[Models]
                complete(models)
              },
              post {
                toStrictEntity(timeOut seconds) { // this kills streams let' revisit
                  formFields("id", "project".?, "description".?) {
                    (id, project, description) =>
                      fileUpload("file") {
                        case (metadata, byteSource) =>
                          val uploadDir = dataUploadDir
                          val sink = FileIO.toPath(
                            Paths.get(uploadDir) resolve metadata.fileName)
                          val uploaded = byteSource.runWith(sink)
                          onSuccess(uploaded) {
                            file =>
                              // todo: this should live in another handler
                              file.status match {
                                case Success(_) =>
                                  val path = s"$uploadDir/${metadata.fileName}"
                                  val pMML = readPMML(new File(path))
                                  val evaluator = getEvaluator(pMML)
                                  var inputFields = Set.empty[InputField]

                                  evaluator.getInputFields.asScala
                                    .foreach { i =>
                                      inputFields += InputField(
                                        i.getName.toString,
                                        i.getDataType.toString,
                                        i.getOpType.toString)
                                    }

                                  val maybeAlgorithm: Future[String] =  Future(pMML.getModels.get(0).getAlgorithmName)

                                  onSuccess(maybeAlgorithm) {
                                    algorithm =>
                                      println(algorithm)
                                      println(algorithm.isEmpty)
                                      algorithm.isEmpty match {
                                          // this indicates the pMML file is messed up
                                        case false => val modelCreated
                                        : Future[ModelActionPerformed] =
                                          (modelRegistryActor ? CreateModel(
                                            Model(
                                              id = Slugify(id),
                                              project = project,
                                              description = description,
                                              path = path,
                                              algorithm = Some(pMML.getModels
                                                .get(0)
                                                .getAlgorithmName),
                                              inputFields = Some(inputFields.toList)
                                            ))).mapTo[ModelActionPerformed]

                                          onSuccess(modelCreated) {
                                            performed =>
                                              log.info(
                                                s"received ${file.count} bytes of '${metadata.fileName}'")
                                              log.info(performed.description)
                                              // todo: fix this, too messy
                                              complete(201 -> HttpEntity(
                                                ContentTypes.`application/json`,
                                                s"""{"description":"${performed.description}"}"""))
                                          }
                                        case _ => complete(HttpEntity(ContentTypes.`application/json`,
                                          """"{"error":"Error registering pMML. Please check your model"}"""))
                                      }
                                  }
                                case Failure(e) =>
                                  throw e //todo: Need msg. to client and graceful fail here.
                              }
                          }
                      }
                  }
                }
              }
            )œ

        } ~
          path(Segments) { segments =>
            val id = segments.head
            segments.size match {
              case 2 => {
                val action = segments(1)
                action match {
                  case "batch" =>
                    toStrictEntity(2 seconds) {
                      post {
                        val maybeModel: Future[Option[Model]] =
                          (modelRegistryActor ? GetModel(id))
                            .mapTo[Option[Model]]
                        val maybeEvaluator: Future[Evaluator] = maybeModel.map {
                          model =>
                            getEvaluator(readPMML(new File(model.get.path)))
                        }
                        case class Observation(data: String,
                                               evaluator: Evaluator)
                        formFields("fields".?) { (fields) =>
                          //todo: implement fields for alpakka
                          fileUpload("csv") {
                            case (metadata, byteSource) =>
                              //todo: test this, compact this, convert to flows
                              val uploadDir = "/tmp"
                              val filePath = s"$uploadDir/${metadata.fileName}"

                              val sink = FileIO.toPath(
                                Paths.get(uploadDir) resolve metadata.fileName)
                              val uploaded = byteSource.runWith(sink)

                              onComplete(uploaded) {
                                case Success(_) =>
                                  onComplete(maybeEvaluator) {
                                    case Success(evaluator) =>
                                      // todo: change this to a runnable graph
                                      val source =
                                        FileIO
                                          .fromPath(Paths.get(filePath))
                                          .via(CsvParsing.lineScanner())
                                          .via(CsvToMap.toMap())
                                          .map(_.mapValues(_.utf8String.trim))
                                          .map(_.values)
                                          .map(_.mkString(","))
                                          .map(str => (str, evaluator))
                                          .mapAsync(2)(params => {
                                            (modelRegistryActor ? GetEstimate(
                                              params._1,
                                              params._2))
                                              .mapTo[ModelActionPerformed]
                                          })
                                          .map(_.description)
                                          .map(s => s + "\n")
                                          .map(ByteString(_))

                                      complete(
                                        HttpEntity(
                                          ContentTypes.`application/json`,
                                          source))

                                    case Failure(_) =>
                                      complete(
                                        s"An error occurred parsing the file: model '$id' does not exist")
                                  }
                                case Failure(ex) =>
                                  complete(
                                    s"An error occurred uploading the file: ${ex.getMessage}")
                              }
                          }
                        }
                      }
                    }
                  case _ => complete(action)
                }
              }
              case _ =>
                concat(
                  get {
                    val maybeModel: Future[Option[Model]] =
                      (modelRegistryActor ? GetModel(id)).mapTo[Option[Model]]
                    rejectEmptyResponse {
                      complete(maybeModel)
                    }
                  },
                  post {
                    val maybeModel: Future[Option[Model]] =
                      (modelRegistryActor ? GetModel(id)).mapTo[Option[Model]]
                    val maybeEvaluator: Future[Evaluator] = maybeModel.map {
                      model =>
                        getEvaluator(readPMML(new File(model.get.path)))
                    }

                    entity(as[JsValue]) {
                      observation =>
                        onComplete(maybeEvaluator) {
                          case Success(evaluator) =>
                            onComplete(
                              (modelRegistryActor ? GetEstimate(
                                Listify(observation).mkString(","),
                                evaluator)).mapTo[ModelActionPerformed]) {
                              case Success(results) =>
                                complete(
                                  HttpEntity(ContentTypes.`application/json`,
                                             results.description))
                              case Failure(ex) =>
                                complete(
                                  s"error performing prediction: ${ex.getMessage}")
                            }
                          case Failure(ex) =>
                            complete(
                              s"error, model not found: ${ex.getMessage}")
                        }
                    }
                  },
                  delete {
                    val modelDeleted: Future[ModelActionPerformed] =
                      (modelRegistryActor ? DeleteModel(id))
                        .mapTo[ModelActionPerformed]
                    onSuccess(modelDeleted) { performed =>
                      log.info(s"${performed.description}")
                      complete((StatusCodes.OK, performed))
                    }
                  }
                )
            }
          }
      }
    }
}
