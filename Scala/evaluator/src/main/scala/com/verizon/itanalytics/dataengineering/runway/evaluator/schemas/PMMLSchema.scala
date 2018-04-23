package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import com.verizon.itanalytics.dataengineering.runway.evaluator.models._
import com.verizon.itanalytics.dataengineering.runway.models._

// http://dmg.org/pmml/v4-3/GeneralStructure.html
trait PMMLSchema
    extends Taxonomy
    with DataDictionary
    with AssociationModel
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
    with Scorecard
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
      baselineModel: Option[BaselineModel] = None,
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
      scorecard: Option[Scorecard] = None,
      supportVectorMachineModel: Option[SupportVectorMachineModel] = None,
      textModel: Option[TextModel] = None,
      timeSeriesModel: Option[TimeSeriesModel] = None,
      treeModel: Option[TreeModel] = None
  )

  case class Extension(extender: Option[String] = None,
                       name: Option[String] = None,
                       value: Option[String] = None)

  case class Header(
      copyright: Option[String] = None,
      description: Option[String] = None,
      modelVersion: Option[String] = None,
      application: Option[Application] = None,
      annotations: Option[Seq[String]] = None, //revisit, make Extensions (once we see one)
      timeStamp: Option[String] = None)

  case class Annotation(annotation: Option[String] = None) //revisit, make Extensions (once we see one)
  case class Application(name: String, version: Option[String] = None)

}
