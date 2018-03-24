package com.verizon.itanalytics.dataengineering.runway.microservice

import java.io.{File, FileInputStream}
import java.nio.file.Paths
import java.util.logging.Logger

import akka.{Done, NotUsed}
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.pattern.ask
import akka.stream.{ClosedShape, Materializer, Outlet, ThrottleMode}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.javadsl.StreamConverters
import akka.stream.scaladsl.{FileIO, Flow, Framing, GraphDSL, Keep, RunnableGraph, Sink, Source}
import akka.util.{ByteString, Timeout}
import com.verizon.itanalytics.dataengineering.runway.evaluator.Manager.{getEvaluator, readPMML}
import com.verizon.itanalytics.dataengineering.runway.microservice.ModelRegistryActor._
import com.verizon.itanalytics.dataengineering.runway.microservice.utils._
import org.jpmml.evaluator.Evaluator
import spray.json.JsValue

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}


trait ModelRoutes extends JsonSupport {
  implicit def system: ActorSystem

  private val log = Logger.getLogger(this.getClass.getName)
  private implicit val timeout: Timeout = Timeout(5.seconds)

  def modelRegistryActor: ActorRef
  final case class ActionPerformed(description: String)

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
              toStrictEntity(3 seconds) { // this kills streams let' revisit
                formFields("id", "project".?, "description".?) { (id, project, description) =>
                  fileUpload("file") {
                    case (metadata, byteSource) =>
                      // todo: place path in config file
                      val uploadDir = "/tmp"
                      val sink = FileIO.toPath(Paths.get(uploadDir) resolve metadata.fileName)
                      val uploaded = byteSource.runWith(sink)
                      onSuccess(uploaded) { file =>
                        // todo: this should live in another handler
                        file.status match {
                          case Success(_) =>
                            val path = s"$uploadDir/${metadata.fileName}"
                            val pMML = readPMML(new File(path))
                            var inputFields = Set.empty[InputField]
                            getEvaluator(pMML).getInputFields.asScala.foreach { i => {
                              inputFields += InputField(i.getName.toString, i.getDataType.toString, i.getOpType.toString)
                            }
                            }
                            val modelCreated: Future[ModelActionPerformed] =
                              (modelRegistryActor ? CreateModel(Model(
                                id = id,
                                project = project,
                                description = description,
                                path = path,
                                algorithm = Some(pMML.getModels.get(0).getAlgorithmName),
                                inputFields = Some(inputFields.toList)
                              ))).mapTo[ModelActionPerformed]
                            onSuccess(modelCreated) { performed =>
                              log.info(s"received ${file.count} bytes of '${metadata.fileName}'")
                              log.info(performed.description)
                              // todo: fix this, too messy
                              complete(201 -> HttpEntity(ContentTypes.`application/json`,
                                s"""{"description":"${performed.description}"}""")
                              )
                            }
                          case Failure(e) => throw e //todo: Need msg. to client and graceful fail here.
                        }
                      }
                  }
                }
              }
            }
          )
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
                      //todo: check if model exists first
                      val maybeModel: Future[Option[Model]] = (modelRegistryActor ? GetModel(id)).mapTo[Option[Model]]
                      val maybeEvaluator: Future[Evaluator] = maybeModel.map { model => getEvaluator(readPMML(new File(model.get.path))) }
                      formFields("fields".?) { (headers) =>
                        fileUpload("csv") {
                          case (metadata, byteSource) =>
                            //todo: test this, compact this, convert to flows
                            val uploadDir = "/tmp"
                            val sink = FileIO.toPath(Paths.get(uploadDir) resolve metadata.fileName)
                            val uploaded = byteSource.runWith(sink)
                            val filePath = s"$uploadDir/${metadata.fileName}"
                            onSuccess(uploaded) { file =>
                              file.status match {
                                case Success(_) =>
                                  complete(s"Successfully written ${metadata.fileName} @ ${file.count} bytes")
//                                  complete(HttpEntity(ContentTypes.`application/octet-stream`, FileIO.fromPath(Paths.get(filePath), 1024)))
                                case Failure(e) => throw e
                              }
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
                  //todo: check if model exists first
                  val maybeModel: Future[Option[Model]] = (modelRegistryActor ? GetModel(id)).mapTo[Option[Model]]
                    val maybeEvaluator: Future[Evaluator] = maybeModel.map { model => getEvaluator(readPMML(new File(model.get.path))) }

                    entity(as[JsValue]) { observation =>
                      val returnedEstimate: Future[Future[ModelRegistryActor.ModelActionPerformed]] = maybeEvaluator.map { evaluator =>
                        (modelRegistryActor ? GetEstimate(Listify(observation).mkString(","), evaluator)).mapTo[ModelActionPerformed]
                      }
                      complete(returnedEstimate)
                    }
                },
                delete {
                  val modelDeleted: Future[ModelActionPerformed] =
                    (modelRegistryActor ? DeleteModel(id)).mapTo[ModelActionPerformed]
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