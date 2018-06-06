package com.verizon.itanalytics.dataengineering.runway.microservice

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas.PMMLSchema
import spray.json._


object JsonProtocol extends DefaultJsonProtocol with NullOptions {

  case class Model(
      id: Int = 0,
      name: String,
      project: Option[String] = None,
      description: Option[String] = None,
      algorithm: Option[String] = Option("undefined"),
      author: Option[String] = None,
      filePath: String,
      createdOn: Option[String] = Some(
        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date)),
      lastUpdated: Option[String] = Some(
        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))
  )

  case class JsonResponse(
      status: Int,
      timeStamp: Option[String] = Option(LocalDateTime.now().toString),
      msg: String,
      results: Option[Seq[Model]] = None
  )

  implicit object ModelFormat extends JsonFormat[Model] {
    def write(model: Model) = JsObject(
      "id" -> JsNumber(model.id),
      "name" -> JsString(model.name),
      "project" -> JsString(model.project.get),
      "description" -> JsString(model.description.get),
      "algorithm" -> JsString(model.algorithm.get),
      "author" -> JsString(model.author.get),
      "filePath" -> JsString(model.filePath),
      "createdOn" -> JsString(model.createdOn.get),
      "lastUpdated" -> JsString(model.lastUpdated.get)
    )
    def read(json: JsValue): Model = json match {
      case JsObject(fields) =>
        Model(
          id = fields("id").convertTo[Int],
          name = fields("name").convertTo[String],
          project = Option(fields("project").convertTo[String]),
          description = Option(fields("description").convertTo[String]),
          algorithm = Option(fields("algorithm").convertTo[String]),
          author = Option(fields("author").convertTo[String]),
          filePath = fields("filePath").convertTo[String],
          createdOn = Option(fields("createdOn").convertTo[String]),
          lastUpdated = Option(fields("lastUpdated").convertTo[String])
        )
      case _ => throw DeserializationException("Model response expected")
    }
  }


  implicit object JsonResponseFormat extends RootJsonFormat[JsonResponse] {
    def write(jsonResponse: JsonResponse): JsObject = jsonResponse.results match {
      case Some(_) =>
        JsObject(
          "status" -> JsNumber(jsonResponse.status),
          "timeStamp" -> JsString(jsonResponse.timeStamp.get),
          "msg" -> JsString(jsonResponse.msg),
          "results" -> jsonResponse.results.toJson
        )
      case None =>
        JsObject(
          "status" -> JsNumber(jsonResponse.status),
          "timeStamp" -> JsString(jsonResponse.timeStamp.get),
          "msg" -> JsString(jsonResponse.msg)
        )
    }
    def read(json: JsValue): JsonResponse = json match {
      case JsObject(fields) =>
        fields.get("results") match {
          case None =>
            JsonResponse(
              status = fields("status").convertTo[Int],
              timeStamp = Option(fields("timeStamp").convertTo[String]),
              msg = fields("msg").convertTo[String]
            )
          case _ =>
            JsonResponse(
              status = fields("status").convertTo[Int],
              timeStamp = Option(fields("timeStamp").convertTo[String]),
              msg = fields("msg").convertTo[String],
              results = Option(fields("results").convertTo[Seq[Model]])
            )
        }
      case _ => throw DeserializationException("Json response expected")
    }
  }



//  implicit object AnyJsonFormat extends JsonFormat[Any] {
//    def write(x: Any): JsValue = x match {
//      case int: Int                     => JsNumber(int)
//      case long: Long                   => JsNumber(long)
//      case double: Double               => JsNumber(double)
//      case string: String               => JsString(string)
//      case boolean: Boolean if boolean  => JsTrue
//      case boolean: Boolean if !boolean => JsFalse
//    }
//    def read(value: JsValue): Any = value match {
//      case JsNumber(int)    => int.intValue()
//      case JsNumber(long)   => long.longValue()
//      case JsNumber(double) => double.doubleValue()
//      case JsString(string) => string
//      case JsTrue           => true
//      case JsFalse          => false
//    }
//  }


  object JsonSupport extends DefaultJsonProtocol  {
    implicit val ModelFormat: JsonFormat[Model] = jsonFormat9(Model)
    implicit val JsonResponseFormat: RootJsonFormat[JsonResponse] = jsonFormat4(JsonResponse)
  }

  def jsonize(rslts: AnyRef, statusCode: Option[Int] = None): (Int, HttpEntity.Strict) = {
    rslts match {
      case s: String =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          JsonResponse(
            status = statusCode.getOrElse(StatusCodes.OK.intValue),
            msg = s
          ).toJson.compactPrint)

      case j: JsonResponse =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          j.toJson.compactPrint)

      case m: Seq[Model] =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          JsonResponse(
            status = statusCode.getOrElse(StatusCodes.OK.intValue),
            msg = StatusCodes.OK.defaultMessage,
            results = Option(m)
          ).toJson.compactPrint
        )

      case err: Throwable =>
        statusCode.getOrElse(StatusCodes.InternalServerError.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          JsonResponse(
            status =
              statusCode.getOrElse(StatusCodes.InternalServerError.intValue),
            msg = s"${StatusCodes.InternalServerError.defaultMessage}: $err"
          ).toJson.compactPrint
        )
    }
  }
}
