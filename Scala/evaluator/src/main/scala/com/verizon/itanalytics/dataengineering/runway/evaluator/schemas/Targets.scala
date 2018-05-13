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
      extension: Option[Seq[Extension]] = None,
      partition: Option[Seq[Partition]] = None,
      value: Option[String] = None,
      displayValue: Option[String] = None,
      priorProbability: Option[Double] = None,
      defaultValue: Option[Double]
  )

  case class Partition(extension: Option[Seq[Extension]] = None,
                       partitionFieldStats: Option[Seq[PartitionFieldStats]] =
                         None,
                       name: String,
                       number: Option[Double] = None)

  case class PartitionFieldStats(
      extension: Option[Seq[Extension]] = None,
      counts: Option[Counts] = None,
      numericInfo: Option[NumericInfo] = None,
      frequenciesType: Option[FrequenciesType] = None,
      field: String,
      weighted: Option[Int] = Some(0))

  case class Counts(extension: Option[Seq[Extension]] = None,
                    totalFreq: Double,
                    missingFreq: Option[Double] = None,
                    invalidFreq: Option[Double] = None,
                    cardinality: Option[Int] = None)

  case class NumericInfo(extension: Option[Seq[Extension]] = None,
                         quantile: Option[Quantile] = None,
                         minimum: Option[Double] = None,
                         maximum: Option[Double] = None,
                         mean: Option[Double] = None,
                         standardDeviation: Option[Double] = None,
                         median: Option[Double] = None,
                         interQuartileRange: Option[Double] = None)

  case class Quantile(extension: Option[Seq[Extension]] = None,
                      quantileLimit: Double,
                      quantileValue: Double)

  case class FrequenciesType(
      numArray: Array
  )
}
