package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait NaiveBayesModel
    extends MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification
    with TestDistributions {

  case class NaiveBayesModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      bayesInputs: Iterable[BayesInput],
      bayesOutput: BayesOutput,
      localTransformations: Option[LocalTransformation] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      threshold: Double,
      functionName: String,
      algorithmName: Option[String] = None,
      isScorable: Option[Boolean]
  )

  case class BayesInput(
      extension: Option[Seq[Extension]] = None,
      targetValueStats: Iterable[TargetValueStat],
      derivedField: Option[DerivedField] = None,
      pairCounts: Seq[PairCount],
      fieldName: String
  )

  case class TargetValueStat(extension: Option[Seq[Extension]] = None,
                             continuousDistributionType: String,
                             value: String)

  case class PairCount(targetValueCounts: Iterable[TargetValueCount],
                       value: String)

  case class TargetValueCount(extension: Option[Seq[Extension]] = None,
                              value: String,
                              count: Double)

  case class BayesOutput(extension: Option[Seq[Extension]] = None,
                         targetValueCounts: Iterable[TargetValueCount])
}
