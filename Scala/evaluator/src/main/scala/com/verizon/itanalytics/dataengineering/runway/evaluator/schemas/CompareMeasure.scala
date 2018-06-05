package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 05, 2018
*/


trait CompareMeasure extends Extension {
  case class ComparisonMeasure(extension: Option[Seq[Extension]] = None,
                               kind: String,
                               compareFunction: String = "absDiff",
                               minimum: Option[Double] = None,
                               maximum: Option[Double] = None,
                               measure: Option[String] = None)

  implicit object ComparisonMeasureFormat extends JsonFormat[ComparisonMeasure] {
    def write(comparisonMeasure: ComparisonMeasure) = JsObject(
      comparisonMeasure.extension match { case _ => "extender" -> JsArray(comparisonMeasure.extension.get.map(_.toJson).toVector) },
      "kind" -> JsString(comparisonMeasure.kind),
      "compareFunction" -> JsString(comparisonMeasure.compareFunction),
      comparisonMeasure.minimum match { case _ => "minimum" -> JsNumber(comparisonMeasure.minimum.get) },
      comparisonMeasure.maximum match { case _ => "maximum" -> JsNumber(comparisonMeasure.maximum.get) },
      comparisonMeasure.measure match { case _ => "defaultValue" -> JsNumber(comparisonMeasure.measure.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

}
