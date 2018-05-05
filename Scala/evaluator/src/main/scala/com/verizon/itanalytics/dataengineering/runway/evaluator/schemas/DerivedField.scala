package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait DerivedField extends DataDictionary {
  case class DerivedField(name: Option[String] = None,
                          displayName: String,
                          optype: String,
                          dataType: String,
                          intervals: Option[Seq[Interval]] = None,
                          values: Option[Seq[String]] = None)
}
