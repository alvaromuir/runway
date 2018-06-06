package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 25, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/ClusteringModel.html
trait ClusteringModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with LocalTransformation
    with ComparisonMeasure
    with ModelVerification {

  case class ClusteringModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      localTransformations: Option[LocalTransformation] = None,
      comparisonMeasure: ComparisonMeasure,
      clusteringFields: Seq[ClusteringField],
      missingValueWeights: Option[Array] = None,
      cluster: Seq[Cluster],
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      modelClass: String,
      numberOfClusters: Int,
      isScorable: Option[Boolean]
  )


  case class Measure(name: String)

  case class ClusteringField(extension: Option[Seq[Extension]] = None,
                             field: String,
                             isCenterField: String = "true",
                             fieldWeight: Double = 1.0,
                             similarityScale: Option[Double] = None,
                             compareFunction: Option[String] = None)

  case class Cluster(extension: Option[Seq[Extension]] = None,
                     id: Option[String] = None,
                     name: Option[String] = None,
                     size: Option[Int] = None, // this is signed positive only
                     covariances: Option[Matrix] = None,
                     kohonenMap: Option[KohonenMap] = None)

  case class KohonenMap(extension: Option[Seq[Extension]] = None,
                        coord1: Option[Double] = None,
                        coord2: Option[Double] = None,
                        coord3: Option[Double] = None)
}
