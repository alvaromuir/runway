package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 10, 2018
 */

//  http://dmg.org/pmml/v4-3/TreeModel.html
trait TreeModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with EmbeddedModel
    with Array
    with Content
    with ScoreDistribution
    with ModelVerification {

  case class TreeModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      node: Node,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      missingValueStrategy: String = "none",
      missingValuePenalty: Double = 1.0,
      noTrueChildStrategy: String = "returnNullPrediction",
      splitCharacteristic: String = "multiSplit",
      isScorable: Option[Boolean] = Option(true)
  )

  case class Node(extension: Option[Seq[Extension]] = None,
                  predicate: String,
                  partition: Option[Partition] = None,
                  scoreDistributions: Option[Seq[ScoreDistribution]] = None,
                  nodes: Option[Seq[Node]] = None,
                  embeddedModel: Option[EmbeddedModel] = None,
                  id: Option[String] = None,
                  score: Option[String] = None,
                  recordCount: Option[Double] = None,
                  defaultChild: Option[String] = None)

}
