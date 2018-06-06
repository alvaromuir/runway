package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._

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

  implicit object FrequenciesTypeFormat extends JsonFormat[FrequenciesType] {
    def write(frequenciesType: FrequenciesType) = JsObject(
      "numArray" -> frequenciesType.numArray.toJson
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object QuantileFormat extends JsonFormat[Quantile] {
    def write(quantile: Quantile) = JsObject(
      quantile.extension match { case _ => "extension" -> JsArray(quantile.extension.get.map(_.toJson).toVector) },
      "quantileLimit" -> JsNumber(quantile.quantileLimit),
      "quantileValue" -> JsNumber(quantile.quantileValue)
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object NumericInfo extends JsonFormat[NumericInfo] {
    def write(numericInfo: NumericInfo) = JsObject(
      numericInfo.extension match { case _ => "extension" -> JsArray(numericInfo.extension.get.map(_.toJson).toVector) },
      numericInfo.quantiles match { case _ => "quantiles" -> JsArray(numericInfo.quantiles.get.map(_.toJson).toVector) },
      numericInfo.minimum match { case _ => "minimum" -> JsNumber(numericInfo.minimum.get) },
      numericInfo.maximum match { case _ => "maximum" -> JsNumber(numericInfo.maximum.get) },
      numericInfo.mean match { case _ => "mean" -> JsNumber(numericInfo.mean.get) },
      numericInfo.standardDeviation match { case _ => "standardDeviation" -> JsNumber(numericInfo.standardDeviation.get) },
      numericInfo.median match { case _ => "median" -> JsNumber(numericInfo.median.get) },
      numericInfo.interQuartileRange match { case _ => "interQuartileRange" -> JsNumber(numericInfo.interQuartileRange.get) }

    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object PartitionFieldStats extends JsonFormat[PartitionFieldStats] {
    def write(partitionFieldStats: PartitionFieldStats) = JsObject(
      partitionFieldStats.extension match { case _ => "extension" -> JsArray(partitionFieldStats.extension.get.map(_.toJson).toVector) },
      partitionFieldStats.counts match { case _ => "counts" -> partitionFieldStats.counts.get.toJson },
      partitionFieldStats.numericInfo match { case _ => "numericInfo" -> partitionFieldStats.numericInfo.get.toJson },
      partitionFieldStats.frequenciesType match { case _ => "frequenciesType" -> partitionFieldStats.frequenciesType.get.toJson },
      "field" -> JsString(partitionFieldStats.field),
      partitionFieldStats.weighted match { case _ => "weighted" -> JsString(partitionFieldStats.weighted.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object PartitionFormat extends JsonFormat[Partition] {
    def write(partition: Partition) = JsObject(
      partition.extension match { case _ => "extension" -> JsArray(partition.extension.get.map(_.toJson).toVector) },
      partition.partitionFieldStats match { case _ => "partitionFieldStats" -> JsArray(partition.partitionFieldStats.get.map(_.toJson).toVector) },
      "name" -> JsString(partition.name),
      partition.size match { case _ => "size" -> JsNumber(partition.size.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object MultivariateStatFormat extends JsonFormat[MultivariateStat] {
    def write(multivariateStat: MultivariateStat) = JsObject(
      "name" -> JsString(multivariateStat.name),
      multivariateStat.category match { case _ => "category" -> JsString(multivariateStat.category.get) },
      "exponent" -> JsNumber(multivariateStat.exponent),
      "isIntercept" -> JsBoolean(multivariateStat.isIntercept),
      multivariateStat.importance match { case _ => "importance" -> JsNumber(multivariateStat.importance.get) },
      multivariateStat.stdError match { case _ => "stdError" -> JsNumber(multivariateStat.stdError.get) },
      multivariateStat.tValue match { case _ => "tValue" -> JsNumber(multivariateStat.tValue.get) },
      multivariateStat.chiSquareValue match { case _ => "chiSquareValue" -> JsNumber(multivariateStat.chiSquareValue.get) },
      multivariateStat.fStatistic match { case _ => "fStatistic" -> JsNumber(multivariateStat.fStatistic.get) },
      multivariateStat.dF match { case _ => "dF" -> JsNumber(multivariateStat.dF.get) },
      multivariateStat.pValueAlpha match { case _ => "pValueAlpha" -> JsNumber(multivariateStat.pValueAlpha.get) },
      multivariateStat.pValueInitial match { case _ => "pValueInitial" -> JsNumber(multivariateStat.pValueInitial.get) },
      multivariateStat.pValueFinal match { case _ => "size" -> JsNumber(multivariateStat.pValueFinal.get) },
      "confidenceLevel" -> JsNumber(multivariateStat.confidenceLevel),
      multivariateStat.confidenceLowerBound match { case _ => "confidenceLowerBound" -> JsNumber(multivariateStat.confidenceLowerBound.get) },
      multivariateStat.confidenceUpperBound match { case _ => "confidenceUpperBound" -> JsNumber(multivariateStat.confidenceUpperBound.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object MultivariateStatsFormat extends JsonFormat[MultivariateStats] {
    def write(multivariateStats: MultivariateStats) = JsObject(
      multivariateStats.targetCategory match { case _ => "targetCategory" -> JsString(multivariateStats.targetCategory.get) },
      "mutlivariatetStats" -> JsArray(multivariateStats.multivariateStats.map(_.toJson).toVector)
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object AnovaRowFormat extends JsonFormat[AnovaRow] {
    def write(anovaRow: AnovaRow) = JsObject(
      "type" -> JsString(anovaRow.`type`),
      "sumOfSquares" -> JsNumber(anovaRow.sumOfSquares),
      "degreesOfFreedom" -> JsNumber(anovaRow.degreesOfFreedom),
      anovaRow.meanOfSquares match { case _ => "meanOfSquares" -> JsNumber(anovaRow.meanOfSquares.get ) },
      anovaRow.fValue match { case _ => "fValue" -> JsNumber(anovaRow.fValue.get ) },
      anovaRow.pValue match { case _ => "pValue" -> JsNumber(anovaRow.pValue.get ) }

    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object AnovaFormat extends JsonFormat[Anova] {
    def write(anova: Anova) = JsObject(
      "target" -> JsString(anova.target),
      anova.anovaRow match { case _ => "anovaRow" -> JsArray(anova.anovaRow.get.map(_.toJson).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object ContStatsFormat extends JsonFormat[ContStats] {
    def write(contStats: ContStats) = JsObject(
      contStats.intervals match { case _ => "intervals" -> JsArray(contStats.intervals.get.map(_.toJson).toVector) },
      "totalValueSum" -> JsNumber(contStats.totalValueSum),
      "totalSquaresSum" -> JsNumber(contStats.totalSquaresSum)
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object DiscStatsFormat extends JsonFormat[DiscStats] {
    def write(discStats: DiscStats) = JsObject(
      discStats.arrays match { case _ => "arrays" -> JsArray(discStats.arrays.get.map(JsString(_)).toVector) },
      discStats.modalValue match { case _ => "modalValue" -> JsString(discStats.modalValue.get) }
      )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object UnivariateStatsFormat extends JsonFormat[UnivariateStats] {
    def write(univariateStats: UnivariateStats) = JsObject(
      "field" -> JsString(univariateStats.field),
      "weighted" -> JsString(univariateStats.weighted),
      univariateStats.counts match { case _ => "counts" -> univariateStats.counts.get.toJson },
      univariateStats.numericInfo match { case _ => "numericInfo" -> univariateStats.numericInfo.get.toJson },
      univariateStats.discStats match { case _ => "discStats" -> univariateStats.discStats.get.toJson },
      univariateStats.contStats match { case _ => "contStats" -> univariateStats.contStats.get.toJson },
      univariateStats.anova match { case _ => "anova" -> univariateStats.anova.get.toJson }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object ModelStatsFormat extends JsonFormat[ModelStats] {
    def write(modelStats: ModelStats) = JsObject(
      modelStats.univariateStats match { case _ => "extension" -> JsArray(modelStats.univariateStats.get.map(_.toJson).toVector) },
      modelStats.multivariateStats match { case _ => "multivariateStats" -> JsArray(modelStats.multivariateStats.get.map(_.toJson).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }


}
