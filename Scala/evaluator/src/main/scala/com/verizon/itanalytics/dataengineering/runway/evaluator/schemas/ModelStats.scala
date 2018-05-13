package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import org.dmg.pmml.NumericInfo

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 25, 2018
*/

// http://dmg.org/pmml/v4-3/Statistics.html
trait ModelStats extends DataDictionary with Counts with Targets {

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
                       totalValueSom: Double,
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
}
