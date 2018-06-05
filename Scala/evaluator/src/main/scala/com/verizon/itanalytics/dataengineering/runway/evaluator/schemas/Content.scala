package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 12, 2018
*/


trait Content {
  case class Locator(publicId: Option[String] = None,
                     systemId: Option[String] = None,
                     lineNumber: Int = 1,
                     columnNumber: Int = 1)
  case class Content(locator: Option[Locator] = None)

  implicit object LocatorFormat extends JsonFormat[Locator] {
    def write(locator: Locator) = JsObject(
      locator.publicId match { case _ => "publicId" -> JsString(locator.publicId.get) },
      locator.systemId match { case _ => "systemId" -> JsString(locator.systemId.get) },
      "lineNumber" -> JsNumber(locator.lineNumber),
      "columnNumber" -> JsNumber(locator.columnNumber)
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object ContentFormat extends JsonFormat[Content] {
    def write(content: Content) = JsObject(
      content.locator match { case _ => "publicId" -> content.locator.toJson }
    )
    def read(json: JsValue): Null = null // not implemented
  }
}
