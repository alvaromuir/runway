package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

trait NearestNeighborModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with CompareMeasure
    with ModelVerification
    with TestDistributions
    with TrainingInstances {

  case class NearestNeighborModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      trainingInstances: Option[TrainingInstances] = None,
      comparisonMeasure: Option[ComparisonMeasure] = None,
      knnInputs: Option[Iterable[KNNInput]] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      numberOfNeighbors: Int,
      continuousScoringMethod: String = "average",
      categoricalScoringMethod: String = "majorityVote",
      instanceIdVariable: Option[String] = None,
      threshold: Double,
      isScorable: Option[Boolean]
  )

  case class KNNInput(extension: Option[Seq[Extension]] = None,
                      field: String,
                      fieldWeight: Double = 1.0,
                      compareFunction: Option[String] = None)

}
