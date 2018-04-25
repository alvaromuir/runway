package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import com.verizon.itanalytics.dataengineering.runway.evaluator.models._

// http://dmg.org/pmml/v4-3/GeneralStructure.html
trait PMMLSchema
    extends Taxonomy
    with DataDictionary
    with AssociationRules
    with BayesianNetwork
    with BaselineModels
    with ClusterModels
    with GeneralRegressionModel
    with GaussianProcess
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
                         associationModel: Option[AssociationRules] = None,
                         baselineModel: Option[BaselineModels] = None,
                         bayesianNetworkModel: Option[BayesianNetwork] = None,
                         clusteringModel: Option[ClusteringModels] = None,
                         gaussianProcessModel: Option[GaussianProcess] = None,
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
