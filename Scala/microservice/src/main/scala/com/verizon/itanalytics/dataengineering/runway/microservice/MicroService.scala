package com.verizon.itanalytics.dataengineering.runway.microservice

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory



import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object MicroService extends App with ProjectRoutes with ModelRoutes {

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")


  implicit val system: ActorSystem = ActorSystem("runwayRestServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val modelRegistryActor: ActorRef = system.actorOf(ModelRegistryActor.props, "modelRegistryActor")
  val projectRegistryActor: ActorRef = system.actorOf(ProjectRegistryActor.props, "projectRegistryActor")

  //  private def versionOneRoute(route: Route) =
  //    pathPrefix("v1") {
  //      route
  //    }

  lazy val routes: Route = projectRoutes ~ modelRoutes

  val bindingFuture = Http().bindAndHandle(routes, host, port)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}