package com.verizon.itanalytics.dataengineering.runway.microservice

import java.io.File

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn


object MicroService extends App with ModelRoutes {

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")


  implicit val system: ActorSystem = ActorSystem("runwayRestServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val modelRegistryActor: ActorRef = system.actorOf(ModelRegistryActor.props, "modelRegistryActor")


  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)
  lazy val routes: Route = modelRoutes

  Http().bindAndHandle(routes, host, port)
}