package com.verizon.itanalytics.dataengineering.runway.microservice

import Tables._

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


object MicroService extends ServiceRoutes {
  private val config: Config = ConfigFactory.load()
  private val appId: String = config.getString("http.appId")
  private val interface: String  = config.getString("http.host")
  private val port:Int = config.getInt("http.port")
  private val logPath: String  = config.getString("logging.path")
  private val logFile: String  = config.getString("logging.file")
  private val logLevel: String = config.getString("akka.logLevel")

  System.setProperty("LOG_PATH", logPath)
  System.setProperty("LOG_FILE", logFile)
  System.setProperty("LOG_LEVEL", logLevel)

  implicit val system: ActorSystem = ActorSystem(s"${appId}RestService")
  val modelRegistry: ActorRef = system.actorOf(ModelRegistry.props, "modelRegistry")


  def main(args: scala.Array[String]): Unit = {

    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport.json().withParallelMarshalling(parallelism = 8, unordered = false)


    initTable().onComplete {
      case Success(_) =>
        log.info("Database successfully seeded.")
        Http()
          .bindAndHandle(
            routes.withAccessLog(accessLog(Logging(system, appId.toUpperCase() + "_ACCESS_LOG"))),
            interface,
            port
          )
          .onComplete {
            case Success(ServerBinding(address)) => println(s"\n\nListening on $address\n\n")
            case Failure(cause)                  => println(s"Can't bind to $interface:$port: $cause")
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

        lazy val routes: Route = serviceRoutes
        def now() = System.nanoTime()

      case Failure(e) =>
        log.error(s"ERROR: Database seeding failed with error message: $e. Now exiting")
        System.exit(1)
    }
  }

}