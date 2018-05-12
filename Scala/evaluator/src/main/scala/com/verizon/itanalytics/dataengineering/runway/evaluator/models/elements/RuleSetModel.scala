package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait RuleSetModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification {

  case class RuleSetModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      ruleSet: RuleSet,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      isScorable: Option[Boolean]
  )

  case class RuleSet(
      extension: Option[Seq[Extension]] = None,
      ruleSectionMethod: Seq[RuleSelectionMethod],
      scoreDistribution: Option[Seq[ScoreDistribution]] = None,
      rules: Seq[Rule],
      recordCount: Option[Double] = None,
      nbCorrect: Option[Double] = None,
      defaultScore: Option[String] = None,
      defaultConfidence: Option[Double] = None
  )

  case class RuleSelectionMethod(
      extension: Option[Seq[Extension]] = None,
      criterion: String
  )

  case class ScoreDistribution(extension: Option[Seq[Extension]] = None,
                               value: String,
                               recordCount: Double,
                               confidence: Option[Double] = None,
                               probability: Option[Double] = None)

  case class Rule(
      simpleRule: Option[SimpleRule] = None,
      compoundRule: Option[CompoundRule] = None
  )

  case class SimpleRule(
      extension: Option[Seq[Extension]] = None,
      scoreDistribution: Option[Seq[ScoreDistribution]] = None,
      id: Option[String] = None,
      score: String,
      recordCount: Option[Double] = None,
      nbCorrect: Option[Double] = None,
      confidence: Option[Double] = None,
      weight: Double = 1.0
  )

  case class CompoundRule(extension: Option[Seq[Extension]] = None, rules: Seq[Rule])
}
