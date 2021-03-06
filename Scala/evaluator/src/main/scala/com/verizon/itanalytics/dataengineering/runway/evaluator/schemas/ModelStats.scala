package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 25, 2018
*/

// http://dmg.org/pmml/v4-3/Statistics.html
trait ModelStats extends DataDictionary with Counts with Array {

  case class ModelStats(
      univariateStats: Option[Seq[UnivariateStats]] = None,
      multivariateStats: Option[Seq[MultivariateStats]] = None)

  case class UnivariateStats(field: String,
                             weighted: String = "0", // this should be an Int
                             counts: Option[Counts] = None,
                             numericInfo: Option[NumericInfo] = None,
                             discStats: Option[DiscStats] = None,
                             contStats: Option[ContStats] = None,
                             anova: Option[Anova] = None)


  case class DiscStats(arrays: Option[Seq[String]] = None,
                       modalValue: Option[String] = None)

  case class ContStats(intervals: Option[Seq[Interval]] = None,
                       totalValueSum: Double,
                       totalSquaresSum: Double)

  case class Anova(target: String, anovaRow: Option[Seq[AnovaRow]] = None)

  case class AnovaRow(`type`: String,
                      sumOfSquares: Double,
                      degreesOfFreedom: Double,
                      meanOfSquares: Option[Double] = None,
                      fValue: Option[Double] = None,
                      pValue: Option[Double] = None)

  case class MultivariateStats(targetCategory: Option[String] = None,
                               multivariateStats: Seq[MultivariateStat])

  case class MultivariateStat(name: String,
                              category: Option[String] = None,
                              exponent: Int = 1,
                              isIntercept: Boolean = false,
                              importance: Option[Double] = None,
                              stdError: Option[Double] = None,
                              tValue: Option[Double] = None,
                              chiSquareValue: Option[Double] = None,
                              fStatistic: Option[Double] = None,
                              dF: Option[Double] = None,
                              pValueAlpha: Option[Double] = None,
                              pValueInitial: Option[Double] = None,
                              pValueFinal: Option[Double] = None,
                              confidenceLevel: Double = 0.95,
                              confidenceLowerBound: Option[Double] = None,
                              confidenceUpperBound: Option[Double] = None)

  case class Partition(extension: Option[Seq[Extension]] = None,
                       partitionFieldStats: Option[Seq[PartitionFieldStats]] = None,
                       name: String,
                       size: Option[Double] = None)

  case class PartitionFieldStats(extension: Option[Seq[Extension]] = None,
                                  counts: Option[Counts] = None,
                                  numericInfo: Option[NumericInfo] = None,
                                  frequenciesType: Option[FrequenciesType] = None,
                                  field: String,
                                  weighted: Option[String] = Option("0"))



  case class NumericInfo(extension: Option[Seq[Extension]] = None,
                         quantiles: Option[Iterable[Quantile]] = None,
                         minimum: Option[Double] = None,
                         maximum: Option[Double] = None,
                         mean: Option[Double] = None,
                         standardDeviation: Option[Double] = None,
                         median: Option[Double] = None,
                         interQuartileRange: Option[Double] = None)

  case class Quantile(extension: Option[Seq[Extension]] = None,
                      quantileLimit: Double,
                      quantileValue: Double)

  case class FrequenciesType(numArray: Array)

}
