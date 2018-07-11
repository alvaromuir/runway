package com.verizon.itanalytics.dataengineering.runway.microservice

import java.io.File
import java.time.LocalDateTime
import java.util

import com.verizon.itanalytics.dataengineering.runway.microservice.utils.Utils
import org.scalatest.{FlatSpec, Matchers}
import JsonProtocol._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import com.verizon.itanalytics.dataengineering.runway.microservice.Tables.{db, initTable, models}
import org.dmg.pmml.{FieldName, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.json4s._
import org.json4s.jackson.JsonMethods._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 06 12, 2018
*/


class JsonProtocolSpec extends FlatSpec with Matchers with Utils {

  val pattern = """^(.*?)\..*""".r

  "the jsonize method" should
    "encode strings to json-encoded HttpEntity objects" in {
    val testString = "this is a test string"
    val jsonEntity = jsonize(testString)
    val pattern(testTimeStamp) = LocalDateTime.now().toString
    val jsonResp = parse(jsonEntity._2.getData.utf8String).extract[JsonResponse]

    jsonEntity._1 shouldEqual StatusCodes.OK.intValue
    jsonEntity._2.contentType shouldEqual ContentTypes.`application/json`

    assert(jsonResp.status == StatusCodes.OK.intValue)
    assert(jsonResp.timeStamp.get.startsWith(testTimeStamp.substring(0, 15)))
    assert(jsonResp.msg.contains(testString))
    assert(jsonResp.pMML.isEmpty)
    assert(jsonResp.results.isEmpty)
    assert(jsonResp.estimate.isEmpty)

  }

  it should "encode pMML Schema objects to json-encoded HttpEntity objects" in {
    val sourceDir = "./microservice/src/test/resources"
    val fileName = "iris_rf.pmml"
    val filePath = s"$sourceDir/$fileName"
    val pMML: PMML = readPMML(new File(filePath))
    val evaluator: ModelEvaluator[_ <: org.dmg.pmml.Model] = evaluatePmml(pMML)

    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)
    val jsonEntity = jsonize(pmmlSchema)
    val pattern(testTimeStamp) = LocalDateTime.now().toString
    val jsonResp = parse(jsonEntity._2.getData.utf8String).values.asInstanceOf[Map[String,Any]]

    jsonEntity._1 shouldEqual StatusCodes.OK.intValue
    jsonEntity._2.contentType shouldEqual ContentTypes.`application/json`

    assert(jsonResp("status") == StatusCodes.OK.intValue)
    assert(jsonResp("timeStamp").toString.startsWith(testTimeStamp.substring(0, 15)))
    assert(jsonResp("msg").toString.contains("OK"))
    assert(jsonResp.get("pMML").nonEmpty)
    assert(jsonResp.get("results").isEmpty)
    assert(jsonResp.get("estimate").isEmpty)

  }

  it should "encode a JsonResponse to json-encoded HttpEntity objects" in {

    val pattern(testTimeStamp) = LocalDateTime.now().toString
    val testString = "this is a JsonResponse msg string"

    val jsonResponse = JsonResponse(
      status = 200,
      timeStamp = Option(testTimeStamp),
      msg = testString
    )

    val jsonEntity = jsonize(jsonResponse)

    jsonEntity._1 shouldEqual StatusCodes.OK.intValue
    jsonEntity._2.contentType shouldEqual ContentTypes.`application/json`
    val jsonResp = parse(jsonEntity._2.getData.utf8String).extract[JsonResponse]

    assert(jsonResp.status == StatusCodes.OK.intValue)
    assert(jsonResp.timeStamp.get.startsWith(testTimeStamp.substring(0, 15)))
    assert(jsonResp.msg.contains(testString))
    assert(jsonResp.pMML.isEmpty)
    assert(jsonResp.results.isEmpty)
    assert(jsonResp.estimate.isEmpty)


  }

  it should "encode a Map of FieldNames to json-encoded HttpEntity objects" in {


    val fieldNameMap: util.Map[String, Any] =
      Map(FieldName.create("test") -> "foo")
        .asInstanceOf[Map[String, Any]]
        .asJava


    val jsonEntity = jsonize(fieldNameMap)

    jsonEntity._1 shouldEqual StatusCodes.OK.intValue
    jsonEntity._2.contentType shouldEqual ContentTypes.`application/json`
    val pattern(testTimeStamp) = LocalDateTime.now().toString
    val jsonResp = parse(jsonEntity._2.getData.utf8String).extract[JsonResponse]

    assert(jsonResp.status == StatusCodes.OK.intValue)
    assert(jsonResp.timeStamp.get.startsWith(testTimeStamp.substring(0, 15)))
    assert(jsonResp.msg.equals("OK"))
    assert(jsonResp.pMML.isEmpty)
    assert(jsonResp.results.isEmpty)
    assert(jsonResp.estimate.contains(Map("test" -> "foo")))
  }


  it should "encode a sequence of Models response to json-encoded HttpEntity objects" in {

    val model = JsonProtocol.Model(
      name = Slugify(s"${this.getClass.getSimpleName} Model Name"),
      project = Some("project"),
      description = Some("description"),
      algorithm = Some("algorithm"),
      author = Some("author"),
      filePath = "/my/filePath"
    )

    initTable()

    db.run(models += model).onComplete {
      case Success(_) =>
        db.run(models.result).onComplete {
          case Success(rows) =>
            val jsonEntity = jsonize(rows)

            jsonEntity._1 shouldEqual StatusCodes.OK.intValue
            jsonEntity._2.contentType shouldEqual ContentTypes.`application/json`
            val pattern(testTimeStamp) = LocalDateTime.now().toString
            val jsonResp = parse(jsonEntity._2.getData.utf8String).extract[JsonResponse]

            assert(jsonResp.status == StatusCodes.OK.intValue)
            assert(jsonResp.timeStamp.get.startsWith(testTimeStamp.substring(0, 15)))
            assert(jsonResp.msg.equals("OK"))
            assert(jsonResp.pMML.isEmpty)
            assert(jsonResp.results.nonEmpty)
            assert(jsonResp.estimate.isEmpty)

          case Failure(e) =>
            val errMsg = s"ERROR listing all models: $e"
            log.error(errMsg)
        }
      case Failure(e) =>
        val errMsg = s"ERROR inserting a new model: $e"
        log.error(errMsg)
    }

  }

  it should "encode a throwable error to json-encoded HttpEntity objects" in {
    val errMsg = s"${StatusCodes.InternalServerError.defaultMessage} Attempting to jsonize invalid type"
    val jsonEntity = jsonize(false.asInstanceOf[AnyRef])
    jsonEntity._1 shouldEqual StatusCodes.InternalServerError.intValue
    jsonEntity._2.contentType shouldEqual ContentTypes.`application/json`
    val pattern(testTimeStamp) = LocalDateTime.now().toString
    val jsonResp = parse(jsonEntity._2.getData.utf8String).extract[JsonResponse]

    assert(jsonResp.status == StatusCodes.InternalServerError.intValue)
    assert(jsonResp.timeStamp.get.startsWith(testTimeStamp.substring(0, 15)))
    assert(jsonResp.msg.equals(errMsg))
    assert(jsonResp.pMML.isEmpty)
    assert(jsonResp.results.isEmpty)
    assert(jsonResp.estimate.isEmpty)
  }

}
