package com.verizon.itanalytics.dataengineering.runway.microservice

import java.io.File
import java.nio.file.Paths
import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.pattern.ask
import akka.stream.scaladsl.FileIO
import akka.util.Timeout

import com.verizon.itanalytics.dataengineering.runway.evaluator.Manager.readPMML
import com.verizon.itanalytics.dataengineering.runway.microservice.ModelRegistryActor._

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}


trait ModelRoutes extends JsonSupport {
  implicit def system: ActorSystem
  private val log = Logger.getLogger(this.getClass.getName)
  private implicit val timeout: Timeout = Timeout(5.seconds)

  def modelRegistryActor: ActorRef

  lazy val modelRoutes: Route =
    pathPrefix("models") {
      extractRequestContext { ctx =>
        implicit val materializer = ctx.materializer
        concat(
          pathEnd {
            concat(
              get {
                val models = (modelRegistryActor ? GetModels).mapTo[Models]
                complete(models)
              },
              post {
                toStrictEntity(5 seconds) {
                  formFields("id", "project".?, "description".?) { (id, project, description) =>
                    fileUpload("file") {
                      case (metadata, byteSource) =>
                        // todo: plase path in config file
                        val path = "/tmp"
                        val sink = FileIO.toPath(Paths.get(path) resolve metadata.fileName)
                        val uploaded = byteSource.runWith(sink)
                        onSuccess(uploaded) { file =>
                          // todo: this should live in another handler
                          file.status match {
                            case Success(_) => {
                              val filePath = s"$path/${metadata.fileName}"
                              val pMML = readPMML(new File(filePath))
                              val algorithm = pMML.getModels.get(0).getAlgorithmName
                              val modelCreated: Future[ModelActionPerformed] =
                                (modelRegistryActor ? CreateModel(Model(id,filePath, Some(algorithm), project,description)))
                                  .mapTo[ModelActionPerformed]
                              onSuccess(modelCreated) { performed =>
                                log.info(s"received ${file.count} bytes of '${metadata.fileName}'")
                                log.info(performed.description)
                                complete(s"uploaded '${metadata.fileName}' and created model '$id'")
                              }
                            }
                            case Failure(e) => throw e // Need msg. to client and graceful fail here.
                          }
                        }
                    }
                  }
                }
              },
              put {
                entity(as[Model]) { model =>
                  val modelCreated: Future[ModelActionPerformed] =
                    (modelRegistryActor ? CreateModel(model)).mapTo[ModelActionPerformed]
                  onSuccess(modelCreated) { performed =>
                    log.info(performed.description)
                    complete((StatusCodes.Created, performed))
                  }
                }
              }
            )
          },
          path(Segment) { id =>
            concat(
              get {
                val maybeModel: Future[Option[Model]] =
                  (modelRegistryActor ? GetModel(id)).mapTo[Option[Model]]
                rejectEmptyResponse {
                  complete(maybeModel)
                }
              },
              delete {
                val modelDeleted: Future[ModelActionPerformed] =
                  (modelRegistryActor ? DeleteModel(id)).mapTo[ModelActionPerformed]
                onSuccess(modelDeleted) { performed =>
                  log.info(s"Deleted model [$id]: ${performed.description}")
                  complete((StatusCodes.OK, performed))
                }
              }
            )
          }
        )
      }
    }
  }