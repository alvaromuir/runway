package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 24, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements._
import com.verizon.itanalytics.dataengineering.runway.evaluator.models._

// http://dmg.org/pmml/v4-3/GeneralStructure.html
trait PMMLSchema
  extends Extension
    with Taxonomy
    with DataDictionary
    with AssociationRules
    with BayesianNetworkModel
    with BaselineModel
    with ClusteringModel
    with GeneralRegressionModel
    with GaussianProcessModel
    with MiningModel
    with NaiveBayesModel
    with NearestNeighborModel
    with NeuralNetworkModel
    with RegressionModel
    with RuleSetModel
    with ScoreCardModel
    with SequenceModel
    with SupportVectorMachineModel
    with TextModel
    with TimeSeriesModel
    with TreeModel {

  case class PMMLSchema(
      header: Header,
      miningBuildTask: Option[String] = None,
      dataDictionary: DataDictionary,
      transformationDictionary: Option[TransformationDictionary] = None,
      version: String,
      associationModel: Option[AssociationModel] = None,
      bayesianNetworkModel: Option[BayesianNetworkModel] = None,
      clusteringModel: Option[ClusteringModel] = None,
      gaussianProcessModel: Option[GaussianProcessModel] = None,
      generalRegressionModel: Option[GeneralRegressionModel] = None,
      miningModel: Option[MiningModel] = None,
      naiveBayesModel: Option[NaiveBayesModel] = None,
      nearestNeighborModel: Option[NearestNeighborModel] = None,
      neuralNetwork: Option[NeuralNetworkModel] = None,
      regressionModel: Option[RegressionModel] = None,
      ruleSetModel: Option[RuleSetModel] = None,
      sequenceModel: Option[SequenceModel] = None,
      scoreCardModel: Option[ScoreCardModel] = None,
      supportVectorMachineModel: Option[SupportVectorMachineModel] = None,
      textModel: Option[TextModel] = None,
      timeSeriesModel: Option[TimeSeriesModel] = None,
      treeModel: Option[TreeModel] = None
  )
  case class Header(extension: Option[Seq[Extension]] = None,
                    copyright: Option[String] = None,
                    description: Option[String] = None,
                    modelVersion: Option[String] = None,
                    application: Option[Application] = None,
                    annotations: Option[Seq[String]] = None,
                    timeStamp: Option[String] = None)

  case class Application(extension: Option[Seq[Extension]] = None, name: String, version: Option[String] = None)
  case class Annotation(extension: Option[Seq[Extension]] = None, annotation: Option[String] = None)
}
