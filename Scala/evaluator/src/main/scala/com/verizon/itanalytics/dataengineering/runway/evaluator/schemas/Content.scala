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

  implicit object ContentProtocol extends DefaultJsonProtocol {
    implicit val locatorFormat: RootJsonFormat[Locator] =  jsonFormat4(Locator)
    implicit val contentFormat: RootJsonFormat[Content] =  jsonFormat1(Content)
    }
}
