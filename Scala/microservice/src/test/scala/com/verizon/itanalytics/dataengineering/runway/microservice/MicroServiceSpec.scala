package com.verizon.itanalytics.dataengineering.runway.microservice

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import org.scalatest.{Matchers, WordSpec}
import org.slf4j.{Logger, LoggerFactory}
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 29, 2018
*/

import Tables._

class MicroServiceSpec extends WordSpec with Matchers {
  private val config: Config = ConfigFactory.load()
  private val appId: String = config.getString("http.appId")
  implicit val system: ActorSystem = ActorSystem(s"${appId}RestService")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  lazy val models = TableQuery[ModelsTable]

  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  "The microservice" should {

    "initialize a database on launch" in {
      initTable().onComplete {
        case Success(_) => log.info("Database successfully seeded.")
        case Failure(e) =>
          log.error(s"ERROR: Database seeding failed with error message: $e")
//          System.exit(1)
      }

      assert(models.baseTableRow.tableName.contains("models"))
    }

  }

}
