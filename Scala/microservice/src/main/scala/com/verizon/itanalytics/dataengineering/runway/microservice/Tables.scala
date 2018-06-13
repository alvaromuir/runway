package com.verizon.itanalytics.dataengineering.runway.microservice

import com.typesafe.config.{Config, ConfigFactory}
import com.verizon.itanalytics.dataengineering.runway.microservice.JsonProtocol.Model
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 29, 2018
 */

object Tables {

  private val config: Config = ConfigFactory.load()
  private val dataUploadPath: String = config.getString("http.dataUploadPath")
  private val srcDataFileName: String = config.getString("db.srcDataFileName")
  private val srcDataFile = s"$dataUploadPath/$srcDataFileName"

  final case class ModelsTable(tag: Tag) extends Table[Model](tag, "models") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def project = column[Option[String]]("project")
    def description = column[Option[String]]("description")
    def algorithm = column[Option[String]]("algorithm")
    def author = column[Option[String]]("author")
    def filePath = column[String]("filePath")
    def creation_dt = column[Option[String]]("creation_dt")
    def updated_dt = column[Option[String]]("updated_dt")
    def * =
      (id,
       name,
       project,
       description,
       algorithm,
       author,
        filePath,
       creation_dt,
       updated_dt).mapTo[Model]
  }

  lazy val models = TableQuery[ModelsTable]
  lazy val db = Database.forConfig("db")

  /**
    * Helper function that awaits a database execution after desired time period. Defaults to 2 seconds
    *
    * @param action  database query
    * @param timeOut FiniteDuration time period
    * @tparam T Type of database call result
    * @return T Type
    */
  def exec[T](action: DBIO[T], timeOut: FiniteDuration = 2.seconds): T =
    Await.result(db.run(action), timeOut)

  /**
    * A Future that initializes a database and populates table with designated CSV data set
    *
    * @param numRows row limit of insertion, defaults to None - which is equivalent to all rows in CSV data set
    * @param dataSet file path of CSV data set
    * @return Future[Unit]
    */
  def initTable(table: TableQuery[ModelsTable] = models,
                numRows: Option[Int] = None,
                dataSet: String = srcDataFile): Future[Unit] = {
    db.run(table.schema.create)
  }
}
