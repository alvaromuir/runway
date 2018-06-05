package com.verizon.itanalytics.dataengineering.runway.microservice

import java.time.LocalDateTime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import spray.json._

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with NullOptions {

  case class JsonResponse(
      status: Int,
      timeStamp: Option[String] = Option(LocalDateTime.now().toString),
      msg: String,
      models: Option[Seq[Model]] = None,
      results: Option[String] = None
  )

  implicit object jsonResponseFormat extends RootJsonFormat[JsonResponse] {
    override def write(jsonResponse: JsonResponse) = JsObject(
      "status" -> JsNumber(jsonResponse.status),
      "timeStamp" -> JsString(jsonResponse.timeStamp.get),
      "msg" -> JsString(jsonResponse.msg),
      "models" -> JsArray(jsonResponse.models.get.map(_.toJson).toVector),
      "result" ->  null
    )

    override def read(json: JsValue): JsonResponse = ???
  }


  def jsonize(rslts: AnyRef,
              statusCode: Option[Int] = None): (Int, HttpEntity.Strict) = {
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

      case _: Throwable =>
        statusCode.getOrElse(StatusCodes.InternalServerError.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          JsonResponse(
            status =
              statusCode.getOrElse(StatusCodes.InternalServerError.intValue),
            msg = StatusCodes.InternalServerError.defaultMessage
          ).toJson.compactPrint
        )
    }
  }
}
