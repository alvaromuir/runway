package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 05, 2018
*/

trait ComparisonMeasure extends Extension {
  case class ComparisonMeasure(extension: Option[Seq[Extension]] = None,
                               kind: String,
                               compareFunction: String = "absDiff",
                               minimum: Option[Double] = None,
                               maximum: Option[Double] = None,
                               measure: Option[String] = None)
}
