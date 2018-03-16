package com.verizon.itanalytics.dataengineering.runway.microservice

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.pattern.ask
import akka.util.Timeout

import com.verizon.itanalytics.dataengineering.runway.microservice.ProjectRegistryActor._

import scala.concurrent.duration._
import scala.concurrent.Future


trait ProjectRoutes extends JsonSupport {
  implicit def system: ActorSystem
  private val log = Logger.getLogger(this.getClass.getName)
  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  def projectRegistryActor: ActorRef

  lazy val projectRoutes: Route =
    pathPrefix("projects") {
      concat(
        pathEnd {
          concat(
            get {
              val projects = (projectRegistryActor ? GetProjects).mapTo[Projects]
              complete(projects)
            },
            post {
              entity(as[Project]) { project =>
                val projectCreated: Future[ProjectActionPerformed] =
                  (projectRegistryActor ? CreateProject(project)).mapTo[ProjectActionPerformed]
                onSuccess(projectCreated) { performed =>
                  log.info(s"Created Project [${project.name}]: ${performed.description}")
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { name =>
          concat(
            get {
              val maybeProject: Future[Option[Project]] =
                (projectRegistryActor ? GetProject(name)).mapTo[Option[Project]]
              rejectEmptyResponse {
                complete(maybeProject)
              }
            },
            delete {
              val ProjectDeleted: Future[ProjectActionPerformed] =
                (projectRegistryActor ? DeleteProject(name)).mapTo[ProjectActionPerformed]
              onSuccess(ProjectDeleted) { performed =>
                log.info(s"Deleted Project [$name]: ${performed.description}")
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
}