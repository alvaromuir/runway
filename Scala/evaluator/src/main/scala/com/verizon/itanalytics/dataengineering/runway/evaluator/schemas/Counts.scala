package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 13, 2018
 */

trait Counts {
  case class Counts(extension: Option[Seq[Extension]] = None,
                    totalFreq: Double,
                    missingFreq: Option[Double] = None,
                    invalidFreq: Option[Double] = None,
                    cardinality: Option[Int] = None)
}
