package com.verizon.itanalytics.dataengineering.runway.microservice

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.{Config, ConfigFactory}
import de.heikoseeberger.accessus.Accessus._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}


object MicroService extends ModelRoutes {
  private val config: Config = ConfigFactory.load()
  private val interface: String  = config.getString("http.host")
  private val port:Int = config.getInt("http.port")
  private val logPath: String  = config.getString("http.logPath")
  private val logLevel: String = config.getString("akka.logLevel")

  System.setProperty("LOG_PATH", logPath)
  System.setProperty("LOG_LEVEL", logLevel)

  implicit val system: ActorSystem = ActorSystem("runwayRestServer")
  val modelRegistryActor: ActorRef = system.actorOf(ModelRegistryActor.props, "modelRegistryActor")



  def main(args: Array[String]): Unit = {

    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)



    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    Http()
      .bindAndHandle(
        routes.withAccessLog(accessLog(Logging(system, "RUNWAY_ACCESS_LOG"))),
        interface,
        port
      )
      .onComplete {
        case Success(ServerBinding(address)) => println(s"Listening on $address")
        case Failure(cause)                  => println(s"Can't bind to $interface:$port: $cause")
      }
  }

  /** Log HTTP method, path, status and response time in micros to the given log at info level. */
  def accessLog(log: LoggingAdapter): AccessLog[Long, Future[Done]] =
    Sink.foreach {
      case ((req, t0), res) =>
        val h = req.headers.mkString(",")
        val m = req.method.value
        val p = req.uri.path.toString
        val s = res.status.intValue()
        val t = (now() - t0) / 1000
        log.info(s"$m request to $p resulted in $s in $t ms \n $h")
    }

  lazy val routes: Route = modelRoutes
  private def now() = System.nanoTime()

}