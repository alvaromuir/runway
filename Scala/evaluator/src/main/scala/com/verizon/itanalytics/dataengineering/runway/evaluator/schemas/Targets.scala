package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 25, 2018
 */

// http://dmg.org/pmml/v4-3/Targets.html
trait Targets {

  case class Targets(
      field: Option[String] = None,
      optype: Option[String] = None,
      castInteger: Option[String] = None, // round, ceiling, floor
      min: Option[Double] = None,
      max: Option[Double] = None,
      rescaleConstant: Double = 0.0,
      rescaleFactor: Double = 1.0,
      targetValues: Option[Seq[TargetValue]] = None
  )

  case class TargetValue(
      value: Option[String] = None,
      displayValue: Option[String] = None,
      priorProbability: Option[Double] = None,
      defaultValue: Option[Double]
  )
}
