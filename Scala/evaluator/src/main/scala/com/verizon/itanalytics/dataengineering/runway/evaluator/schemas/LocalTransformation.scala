package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait LocalTransformation extends Extension with DerivedField {
  case class LocalTransformation(extension: Option[Seq[Extension]] = None,
                                 derivedFields: Option[Seq[DerivedField]])
}
