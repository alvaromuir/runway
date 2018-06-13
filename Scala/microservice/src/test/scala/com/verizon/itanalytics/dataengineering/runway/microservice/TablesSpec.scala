package com.verizon.itanalytics.dataengineering.runway.microservice

import JsonProtocol._
import MicroService.system
import Tables._
import akka.stream.ActorMaterializer
import utils._

import com.typesafe.config.{Config, ConfigFactory}

import org.scalatest.FlatSpec

import slick.lifted.TableQuery
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 30, 2018
 */

class TablesSpec extends FlatSpec with Utils {

  private val config: Config = ConfigFactory.load()
  private val apiVersion: String = config.getString("http.apiVersion")
  private val dataUploadPath: String = config.getString("http.dataUploadPath")
  private val srcDataFileName: String = config.getString("db.srcDataFileName")
  val pathPrefix = s"/api/$apiVersion"

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val name = "TableSpecTest Model Name"
  val project = Some("Some Project Name")
  val description = Some("Some model description goes here....")
  val algorithm = Some("testing algo")
  val author = Some("someUserId")
  val filePath = s"$dataUploadPath/$srcDataFileName"


  lazy val models = TableQuery[ModelsTable]

  "The Tables object" should
    "insert records into the database (via seedTable)" in {

    val model = Model(
      name = Slugify(s"${this.getClass.getSimpleName} Model Name"),
      project = project,
      description = description,
      algorithm = algorithm,
      author = author,
      filePath= filePath
    )

//    initTable()

    db.run(models += model).onComplete {
      case Success(_) =>
        val infoMsg = s"$name model record created as ${Slugify(name)}."
        log.info(infoMsg)
      case Failure(e) =>
        val errMsg = s"ERROR initializing database: $e"
        log.error(errMsg)
    }

    db.run(models.result).onComplete {
      case Success(rows) =>
        val rslt = rows.head
        assert(rslt.name == Slugify(name))
        assert(rslt.project.contains(Slugify(project.get)))
        assert(rslt.description.contains(description.get))
        assert(rslt.algorithm.contains(algorithm.get))
        assert(rslt.author.contains(author.get))
        assert(rslt.filePath.contains(filePath))
      case Failure(e) =>
        val errMsg = s"ERROR querying database: $e"
        log.error(errMsg)
    }

  }

}
