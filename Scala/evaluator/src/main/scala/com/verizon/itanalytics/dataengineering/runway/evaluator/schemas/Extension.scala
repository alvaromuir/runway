package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait Extension {

  case class Extension(
      extender: Option[String] = None,
      name: Option[String] = None,
      value: Option[String] = None,
      content: Option[Seq[String]] = None
  )

}
