package com.verizon.itanalytics.dataengineering.runway.microservice

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util
import java.util.Date

import akka.http.scaladsl.model._
import com.verizon.itanalytics.dataengineering.runway.evaluator.Evaluator
import org.dmg.pmml.FieldName
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._
import spray.json._

import scala.collection.JavaConverters._
import scala.collection.mutable

object JsonProtocol extends Evaluator {

  case class Model(
      id: Int = 0,
      name: String,
      project: Option[String] = None,
      description: Option[String] = None,
      algorithm: Option[String] = Option("undefined"),
      author: Option[String] = Option("unknown"),
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
      pMML: Option[PMMLSchema] = None,
      results: Option[Seq[Model]] = None,
      estimate: Option[mutable.Map[String, Any]] = None
  )

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats



  // todo: write tests for this, int should probably be last
  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any): JsValue = x match {
      case d: Double        => JsNumber(d)
      case f: FieldName     => JsString(f.getValue)
      case s: String        => JsString(s)
      case b: Boolean if b  => JsTrue
      case b: Boolean if !b => JsFalse
      case _                => JsNull
    }

    def read(value: JsValue): Any = value match {
      case JsNumber(d) => d.doubleValue()
      case JsString(s) => s
      case JsTrue      => true
      case JsFalse     => false
      case JsNull      => null
    }
  }

  /**
    * Returns a Akka-Http HttpEntity object for json-encoded responses
    * @param toEncode Any object to encode
    * @param statusCode optional Http Response code as part of returned entity
    * @return
    */
  def jsonize(toEncode: AnyRef, statusCode: Option[Int] = None): (Int, HttpEntity.Strict) = {
    toEncode match {

      case s: String =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(
            JsonResponse(
              status = statusCode.getOrElse(StatusCodes.OK.intValue),
              msg = s
            )))

      case p: PMMLSchema =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(
            JsonResponse(
              status = statusCode.getOrElse(StatusCodes.OK.intValue),
              msg = StatusCodes.OK.defaultMessage,
              pMML = Some(p)
            ))
        )

      case j: JsonResponse =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(j))

      case l: util.Map[FieldName, _] =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(
            JsonResponse(
              status = statusCode.getOrElse(StatusCodes.OK.intValue),
              msg = StatusCodes.OK.defaultMessage,
              estimate = Some(l.asScala.map {
                case (k, v: Double) => k.getValue -> v
                case (k, v) => k.getValue -> v.toString
              })
            ))
        )

      case m: Seq[Model] =>
        statusCode.getOrElse(StatusCodes.OK.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(
            JsonResponse(
              status = statusCode.getOrElse(StatusCodes.OK.intValue),
              msg = StatusCodes.OK.defaultMessage,
              results = Some(m)
            ))
        )

      case err: Throwable =>
        val errMsg = s"${StatusCodes.InternalServerError.defaultMessage}: $err"
        log.error(errMsg)

        statusCode.getOrElse(StatusCodes.InternalServerError.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(
            JsonResponse(
              status =
                statusCode.getOrElse(StatusCodes.InternalServerError.intValue),
              msg = errMsg
            ))
        )

      case _ =>
        val errMsg = s"${StatusCodes.InternalServerError.defaultMessage} Attempting to jsonize invalid type"
        log.error(errMsg)

        statusCode.getOrElse(StatusCodes.InternalServerError.intValue) -> HttpEntity(
          ContentTypes.`application/json`,
          write(
            JsonResponse(
              status =
                statusCode.getOrElse(StatusCodes.InternalServerError.intValue),
              msg = errMsg
            ))
        )

    }
  }
}
