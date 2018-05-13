package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 12, 2018
 */

// http://dmg.org/pmml/v4-3/Scorecard.html
trait ScoreCardModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification {

  case class ScoreCardModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      characteristics: Iterable[Characteristic],
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      initialScore: Double = 0,
      useReasonCodes: Option[Boolean],
      reasonCodeAlgorithm: String = "pointsBelow",
      baselineScore: Double,
      baselineMethod: String = "other",
      isScorable: Option[Boolean]
  )

  case class Characteristic(extension: Option[Seq[Extension]] = None,
                            attribute: Iterable[Attribute],
                            name: Option[String] = None,
                            reasonCode: String,
                            baselineScore: Double)

  case class Attribute(extension: Option[Seq[Extension]] = None,
                       complexPartialScore: Option[ComplexPartialScore] = None,
                       reasonCode: String,
                       partialScore: Option[Double]
                      )

  case class ComplexPartialScore(extension: Option[Seq[Extension]] = None,
                                 expression: String) // should be Expression
}
