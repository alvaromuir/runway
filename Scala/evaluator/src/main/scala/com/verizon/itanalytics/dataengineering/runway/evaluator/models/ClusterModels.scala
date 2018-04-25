package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/ClusteringModel.html
trait ClusterModels
    extends MiningSchema
    with Output
    with Statistics
    with ModelExplanation
    with Target
    with TransformationDictionary
    with ModelVerification {

  case class ClusteringModels(
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      modelClass: String,
      numberOfClusters: Int,
      isScorable: Option[Boolean],
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Target]] = None,
      localTransformation: Option[LocalTransformation] = None,
      comparisonMeasure: ComparisonMeasure,
      clusteringFields: Seq[ClusteringField],
      missingValueWeights: Option[MissingValueWeights] = None,
      clusters: Seq[Cluster],
      modelVerification: Option[ModelVerification] = None
  )

  case class ComparisonMeasure(kind: String,
                               compareFunction: String = "absDiff",
                               minimum: Option[Double] = None,
                               maximum: Option[Double] = None,
                               measure: Option[String] = None)

  case class Measure(name: String)

  case class ClusteringField(field: String,
                             isCenterField: String = "true",
                             fieldWeight: Double = 1.0,
                             similarityScale: Option[Double] = None,
                             compareFunction: Option[String] = None)

  case class MissingValueWeights(n: Int, `type`: String, value: String)

  case class Cluster(id: Option[String] = None,
                     name: Option[String] = None,
                     size: Option[Int] = None, // this is signed positive only
                     covariances: Option[Matrix] = None,
                     kohonenMap: Option[KohonenMap] = None)

  case class KohonenMap(coord1: Option[Double] = None,
                        coord2: Option[Double] = None,
                        coord3: Option[Double] = None)
}
