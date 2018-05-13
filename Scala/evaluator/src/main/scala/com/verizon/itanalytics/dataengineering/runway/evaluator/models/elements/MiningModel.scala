package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

// http://dmg.org/pmml/v4-3/MultipleModels.html#xsdElement_MiningModel
trait MiningModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification {

  case class MiningModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      embeddedModels: Option[Seq[EmbeddedModels]] = None,
      segmentation: Option[Segmentation] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      isScorable: Option[Boolean]
  )

  case class Segmentation(multipleModelMethod: String,
                          segments: Option[Seq[Segment]] = None,
                          localTransformation: Option[LocalTransformation] =
                            None)

  case class Segment(id: Option[String] = None,
                     weight: Double = 1,
                     predicate: String)

  case class EmbeddedModels(modelName: Option[String] = None,
                            functionName: String,
                            output: Option[Output] = None,
                            modelStats: Option[ModelStats] = None,
                            targets: Option[Iterable[Targets]] = None,
                            localTransformation: Option[LocalTransformation] =
                              None)

  case class Regression(modelName: Option[String] = None,
                        functionName: String,
                        algorithmName: Option[String] = None,
                        normalizationMethod: String = "none")

  case class DecisionTree(modelName: Option[String] = None,
                          functionName: String,
                          algorithmName: Option[String] = None,
                          missingValueStrategy: String = "none",
                          missingValuePenalty: Double = 1.0,
                          noTrueChildStrategy: String = "returnNullPrediction",
                          splitCharacteristic: String = "multiSplit")
}
