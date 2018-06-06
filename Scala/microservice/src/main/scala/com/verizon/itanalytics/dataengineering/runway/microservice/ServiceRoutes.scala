package com.verizon.itanalytics.dataengineering.runway.microservice


import MicroService.system
import Tables._
import utils.Utils

import com.verizon.itanalytics.dataengineering.runway.evaluator.Evaluator

import java.io.File
import java.nio.file.Paths

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.scaladsl.FileIO
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout

import com.typesafe.config.{Config, ConfigFactory}

import JsonProtocol._

import slick.jdbc.H2Profile.api._



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

trait ServiceRoutes extends Evaluator with Utils {

  import akka.http.scaladsl.server.Directives._
  final case class ActionPerformed(description: String) //todo:review

  private val config: Config = ConfigFactory.load()
  private val apiVersion: String = config.getString("http.apiVersion")
  private val dataUploadPath: String = config.getString("http.dataUploadPath")
//  private val srcDataFileName: String = config.getString("db.srcDataFileName")
  private val timeOut: Int = config.getInt("http.timeOut")


  private implicit val timeout: Timeout = Timeout(timeOut.seconds)

  def modelRegistry: ActorRef

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
              val resp = "This is just a test. Or is it 😉"
              complete(
                 jsonize(resp))
            }
          } ~
          pathPrefix("models") {
            extractRequestContext { ctx =>
              implicit val materializer: Materializer = ctx.materializer
              pathEnd {
                concat(
                  get {
                    onComplete(db.run(models.result)) {
                      case Success(rows) =>
                        rows.size match {
                          case 0 =>
                            val resp = JsonResponse(
                              status = StatusCodes.OK.intValue,
                              msg = StatusCodes.OK.defaultMessage,
                              results = Option(rows)
                            )
                            complete(jsonize(resp))
                          case _ =>
                            val resp = JsonResponse(
                              status = StatusCodes.OK.intValue,
                              msg = StatusCodes.OK.defaultMessage,
                              results = Option(rows)
                            )
                            complete(jsonize(resp))
                        }
                      case Failure(e) =>
                        val errMsg = s"ERROR listing all models: $e"
                        log.error(errMsg)
                        complete(jsonize(e))
                    }
                  } ~
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
                                case Success(file) =>
                                  val filePath = s"$dataUploadPath/${metadata.fileName}"
                                  val pMML = readPMML(new File(filePath))
                                  val evaluator = evaluatePmml(pMML)

                                  val infoMsg = s"received ${file.count} bytes of '${metadata.fileName}'"
                                  log.info(infoMsg)

                                  onComplete(db.run(models += Model(
                                    name = Slugify(name),
                                    project = project,
                                    description = description,
                                    author = author,
                                    filePath = filePath,
                                    algorithm = Option(evaluator.getSummary)
                                  ))) {
                                    case Success(_) =>
                                      val infoMsg = s"model: $name record created as '${Slugify(name)}'."
                                      log.info(infoMsg)
                                      complete(jsonize(infoMsg, Option(StatusCodes.Created.intValue)))
                                    case Failure(e) =>
                                      val errMsg = s"ERROR submitting model: $e"
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
                    }
                )
              } ~
              path(Segments) {
                segments =>
                val name = segments.head
                segments.size match {
                  case 2 => {
                    val action = segments(1)
                    action match {
                      case "batch" =>
                        toStrictEntity(2 seconds) {
                          post {
                            complete("posted")
                          }
                        }
                      case "details" =>
                        get {
                          onComplete(db.run(models.filter(_.name === name).result)) {
                            case Success(rslts) =>
                              val maybePmml = Future(parsePmml(readPMML(new File(rslts.head.filePath))))
                              onComplete(maybePmml) {
                                case Success(pMML) =>
//                                  println(pMML.to)
                                  complete(jsonize(s"$name details"))
                                case Failure(e) =>
                                  val errMsg = s"ERROR retrieving the model source: $e"
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
                  }
                  case 1 => concat(
                    get {
                      onComplete(db.run(models.filter(_.name === name).result)) {
                        case Success(rslts) =>
                          complete(jsonize(rslts))
                        case Failure(e) =>
                          val errMsg = s"ERROR uploading model: $e"
                          log.error(errMsg)
                          complete(jsonize(e))
                      }
                    } ~
                    put { complete(jsonize(s"updated $name")) } ~
                    delete { complete(jsonize(s"deleted $name")) }
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
