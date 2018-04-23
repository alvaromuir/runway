package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

// http://dmg.org/pmml/v4-3/Targets.html
trait Target {

  case class Target(
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
