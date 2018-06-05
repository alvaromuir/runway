package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._
/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 13, 2018
 */

trait Counts extends Extension {
  case class Counts(extension: Option[Seq[Extension]] = None,
                    totalFreq: Double,
                    missingFreq: Option[Double] = None,
                    invalidFreq: Option[Double] = None,
                    cardinality: Option[Int] = None)

  implicit object CountsFormat extends JsonFormat[Counts] {
    def write(counts: Counts) = JsObject(
      counts.extension match { case _ => "extender" -> JsArray(counts.extension.get.map(_.toJson).toVector) },
      "totalFreq" -> JsNumber(counts.totalFreq),
      counts.missingFreq match { case _ => "missingFreq" -> JsNumber(counts.missingFreq.get) },
    counts.invalidFreq match { case _ => "invalidFreq" -> JsNumber(counts.invalidFreq.get) },
    counts.cardinality match { case _ => "cardinality" -> JsNumber(counts.cardinality.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }
}

