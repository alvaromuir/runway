package com.verizon.itanalytics.dataengineering.runway.schemas

// this is used in Baseline, Bayesian
trait TestDistributions extends TransformationDictionary {

  case class TestDistributions(
      field: String,
      testStatistic: String,
      resetValue: Option[Double] = None,
      windowSize: Option[Int] = None,
      weightField: Option[String] = None,
      normalizationScheme: Option[String] = None,
      baseline: Baseline,
      alternate: Option[Alternate] = None
  )

  case class Baseline(fieldRefs: Seq[FieldRef],
                      continuousDistribution: Option[String] = None, // should be choice of ContinuousDistribution see below
                      countTable: Option[CountTable] = None,
                      normalizedCountTable: Option[CountTable])

  case class ContinuousDistribution(
      anyDistribution: Option[AnyDistribution] = None,
      gaussianDistribution: Option[GaussianDistribution] = None,
      logormalDistribution: Option[LognormalDistribution] = None,
      poissonDistribution: Option[PoissonDistribution] = None,
      triangularDistribution: Option[TriangularDistribution] = None,
      uniformDistribution: Option[UniformDistribution] = None)

  case class AnyDistribution(mean: Double, variance: Double)
  case class GaussianDistribution(mean: Double, variance: Double)
  case class LognormalDistribution(mean: Double, variance: Double)
  case class PoissonDistribution(mean: Double)
  case class TriangularDistribution(mean: Double, lower: Double, upper: Double)
  case class UniformDistribution(lower: Double, upper: Double)

  case class DiscreteDistribution(fieldRefs: Seq[FieldRef],
                                  normalizedCountTable: Option[CountTable],
                                  countTable: Option[CountTable])


  case class CountTable(fieldValues: Option[Seq[FieldValue]],
                            fieldValueCounts: Option[Seq[FieldValueCount]],
                            sample: Option[Double] = None)

  case class FieldValue(field: String, value: String)

  case class FieldValueCount(field: String, value: String)

  case class Alternate(continuousDistributionType: String) // should be choice of ContinuousDistribution see above

}
