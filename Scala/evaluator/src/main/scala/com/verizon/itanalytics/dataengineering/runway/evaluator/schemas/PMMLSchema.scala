package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 24, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements._
import com.verizon.itanalytics.dataengineering.runway.evaluator.models._

import spray.json._
import DefaultJsonProtocol._

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


  implicit object ApplicationFormat extends JsonFormat[Application] {
    def write(application: Application) = JsObject(
      application.extension match { case _ => "extension" -> JsArray(application.extension.get.map(_.toJson).toVector) },
      "name" -> JsString(application.name),
      application.version match { case _ => "version" -> JsString(application.version.get) }
    )
    def read(json: JsValue) = null // not implemented
  }

  implicit object Annotation extends JsonFormat[Annotation] {
    def write(annotation: Annotation) = JsObject(
      annotation.extension match { case _ => "extension" -> JsArray(annotation.extension.get.map(_.toJson).toVector) },
      annotation.annotation match { case _ => "annotation" -> JsString(annotation.annotation.get) }
    )
    def read(json: JsValue) = null // not implemented
  }

  implicit object HeaderFormat extends JsonFormat[Header] {
    def write(header: Header) = JsObject(
      header.description match { case _ => "description" -> JsString(header.description.get) },
      header.modelVersion match { case _ => "modelVersion" -> JsString(header.modelVersion.get) },
      header.application match { case _ => "application" -> header.application.get.toJson },
      header.annotations match { case _ => "annotations" -> JsArray(header.annotations.get.map(_.toJson).toVector) },
      header.timeStamp match { case _ => "timeStamp" -> JsString(header.timeStamp.get)}
    )
    def read(json: JsValue) = null // not implemented
  }

  object PMMLSchemaFormat extends JsonFormat[PMMLSchema] {
    def write(pMMLSchema: PMMLSchema) = JsObject(
      "header" -> pMMLSchema.header.toJson,
      pMMLSchema.miningBuildTask match { case _ => "miningBuildTask" -> JsString(pMMLSchema.miningBuildTask.get) },
      "dataDictionary" -> pMMLSchema.dataDictionary.toJson,
      "transformationDictionary" -> JsNull,
      "version" -> JsString(pMMLSchema.version),
      "associationModel" -> JsNull,
      "bayesianNetworkModel" -> JsNull,
      "clusteringModel" -> JsNull,
      "gaussianProcessModel" -> JsNull,
      "generalRegressionModel" -> JsNull,
      "miningModel" -> JsNull,
      "naiveBayesModel" -> JsNull,
      "nearestNeighborModel" -> JsNull,
      "neuralNetwork" -> JsNull,
      "regressionModel" -> JsNull,
      "ruleSetModel" -> JsNull,
      "sequenceModel" -> JsNull,
      "scoreCardModel" -> JsNull,
      "supportVectorMachineModel" -> JsNull,
      "textModel" -> JsNull,
      "timeSeriesModel" -> JsNull,
      "treeModel" -> JsNull
    )
    def read(json: JsValue) = null // not implemented

  }

  object JsonSupport extends DefaultJsonProtocol  {
    implicit val PMMLSchemaFormat: JsonFormat[PMMLSchema] = jsonFormat22(PMMLSchema)
  }

}
