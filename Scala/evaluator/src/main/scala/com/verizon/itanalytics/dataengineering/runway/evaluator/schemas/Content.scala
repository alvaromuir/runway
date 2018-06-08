package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

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
}
