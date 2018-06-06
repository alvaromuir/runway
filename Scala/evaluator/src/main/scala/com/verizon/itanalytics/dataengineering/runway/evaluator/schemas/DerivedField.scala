package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait DerivedField extends DataDictionary {
  case class DerivedField(name: Option[String] = None,
                          displayName: Option[String] = None,
                          optype: String,
                          dataType: String,
                          intervals: Option[Seq[Interval]] = None,
                          values: Option[Seq[String]] = None)

  implicit object DerivedFieldProtocol extends DefaultJsonProtocol {
    implicit val arrayFormat: RootJsonFormat[DerivedField] =  jsonFormat6(DerivedField)
  }
}


