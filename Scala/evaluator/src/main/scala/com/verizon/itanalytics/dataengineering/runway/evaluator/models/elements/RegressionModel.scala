package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 06, 2018
 */

// http://dmg.org/pmml/v4-3/Regression.html#xsdElement_RegressionModel
trait RegressionModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification {

  case class RegressionModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      regressionTable: Seq[RegressionTable],
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      modelType: Option[String] = None,
      targetFieldName: Option[String] = None,
      normalizationMethod: Option[String] = None,
      isScorable: Option[Boolean])

  case class RegressionTable(
      extension: Option[Seq[Extension]] = None,
      numericPredictor: Option[Seq[NumericPredictor]] = None,
      categoricalPredictor: Option[Seq[CategoricalPredictor]] = None,
      predictorTerm: Option[Seq[PredictorTerm]] = None,
      intercept: Double,
      targetCategory: Option[String] = None)

  case class NumericPredictor(extension: Option[Seq[Extension]] = None,
                              name: Option[String] = None,
                              exponent: Int = 1,
                              coefficient: Double)

  case class CategoricalPredictor(extension: Option[Seq[Extension]] = None,
                                  name: Option[String] = None,
                                  value: Option[String] = None,
                                  coefficient: Double)

  case class PredictorTerm(extension: Option[Seq[Extension]] = None,
                           name: Option[String] = None,
                           coefficient: Double)
}
