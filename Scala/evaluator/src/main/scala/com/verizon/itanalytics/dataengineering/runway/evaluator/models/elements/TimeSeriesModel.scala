package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 10, 2018
 */

// http://dmg.org/pmml/v4-3/TimeSeriesModel.html
trait TimeSeriesModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with Array
    with Content
    with ModelVerification {

  case class TimeSeriesModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      timeSeries: Option[Iterable[TimeSeries]] = None,
      spectralAnalysis: Option[SpectralAnalysis] = None,
      armia: Option[Armia] = None,
      exponentialSmoothing: Option[ExponentialSmoothing] = None,
      seasonalTrendDecomposition: Option[SeasonalTrendDecomposition] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      bestFit: String,
      isScorable: Option[Boolean] = Option(true)
  )

  case class TimeSeries(
      timeAnchor: Option[TimeAnchor] = None,
      timeValues: Option[Seq[TimeValue]] = None,
      usage: String = "original",
      startTime: Double,
      endTime: Double,
      InterpolationMethod: String = "none"
  )

  case class TimeAnchor(timeCycles: Option[Seq[TimeCycle]] = None,
                        timeException: Option[Seq[TimeException]] = None,
                        `type`: Option[String] = None,
                        offset: Option[Int] = None,
                        stepsize: Option[Int] = None,
                        displayName: Option[String] = None)

  case class TimeCycle(array: IntArray,
                       length: Int,
                       `type`: String,
                       displayName: Option[String] = None)

  case class TimeException(array: IntArray,
                           `type`: String,
                           count: Option[Int] = None)

  case class SpectralAnalysis(name: String = "SpectralAnalysis")

  case class Armia(name: String = "ARIMA")

  case class SeasonalTrendDecomposition(
      name: String = "SeasonalTrendDecomposition")

  case class ExponentialSmoothing(
      level: Option[Level],
      trendExpoSmooth: Option[TrendExpoSmooth] = None,
      seasonalityExpoSmooth: Option[SeasonalityExpoSmooth] = None,
      timeValues: Option[Seq[TimeValue]] = None,
      rmse: Double,
      transformation: String = "none")

  case class Level(alpha: Option[Double] = None, smoothedValue: Double)

  case class TrendExpoSmooth(trend: String = "additive",
                             gamma: Option[Double] = None,
                             phi: Option[Double] = Option(1.0),
                             smoothedValue: Option[Double] = None)

  case class SeasonalityExpoSmooth(`type`: String,
                                   period: Int,
                                   unit: Option[Double] = None,
                                   phase: Option[Int] = None,
                                   delta: Option[Double] = None)

  case class TimeValue(timeStamp: Option[Iterable[String]],
                       index: Option[Int] = None,
                       value: Double,
                       standardError: Option[Double] = None)

}
