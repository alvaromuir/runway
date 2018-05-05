package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 27, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas.{
  MiningSchema,
  ModelExplanation,
  ModelStats,
  ModelVerification,
  Output,
  _
}

// http://dmg.org/pmml/v4-3/GeneralRegression.html
trait GeneralRegressionModel
    extends MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification {

  case class GeneralRegression(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      parameterList: Option[Seq[Parameter]],
      factorsList: Option[Seq[Predictor]] = None,
      covariateList: Option[Seq[Predictor]] = None,
      pPMatrix: Option[Seq[PPCell]] = None,
      pCovMatrix: Option[PCovMatrix] = None,
      paramMatrix: Option[Seq[PCell]] = None,
      eventValues: Option[EventValues] = None,
      baseCumHazardTables: Option[BaseCumHazardTable] = None,
      modelVerification: Option[ModelVerification] = None,
      targetVariableName: Option[String] = None,
      modelType: String,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      targetReferenceCategory: Option[String] = None,
      cumulativeLink: Option[String] = None,
      linkFunction: Option[String] = None,
      linkParameter: Option[Double] = None,
      trialsVariable: Option[String] = None,
      trialsValue: Option[Int] = None,
      distribution: Option[String] = None,
      distParameter: Option[Double] = None,
      offsetVariable: Option[String] = None,
      offsetValue: Option[Double] = None,
      modelDF: Option[Double] = None,
      endTimeVariable: Option[String] = None,
      startTimeVariable: Option[String] = None,
      subjectIDVariable: Option[String] = None,
      statusVariable: Option[String] = None,
      baselineStrataVariable: Option[String] = None,
      isScorable: Option[Boolean]
  )

  case class Parameter(name: String,
                       label: Option[String] = None,
                       key: Option[String] = None,
                       referencePoint: Double = 0.0)

  case class Predictor(name: String,
                       contrastMatrixType: Option[String] = None,
                       matrix: Option[Matrix] = None,
                       categories: Option[String] = None)

  case class PPCell(predictorName: String,
                    parameterName: String,
                    value: Option[String] = None,
                    targetCategory: Option[String] = None)

  case class PCovMatrix(`type`: String, pCovCells: Option[Seq[PCovCell]] = None)

  case class PCell(targetCategory: Option[String] = None,
                   parameterName: String,
                   beta: Double,
                   df: Int)

  case class PCovCell(
      pRow: String,
      pCol: String,
      tRow: Option[String] = None,
      tCol: Option[String] = None,
      value: Double,
      targetCategory: Option[String] = None
  )

  case class EventValues(values: Option[Seq[EventValue]] = None,
                         intervals: Option[Seq[Interval]] = None)

  case class EventValue(displayValue: String,
                        key: String,
                        property: Option[String] = None,
                        value: Option[String] = None)

  case class BaseCumHazardTable(maxTime: Option[Double] = None,
                                baselineStratum: Option[Seq[BaselineStratum]] =
                                  None,
                                baselineCells: Option[Seq[BaselineCell]] = None)

  case class BaselineStratum(value: String,
                             label: Option[String] = None,
                             maxTime: Double = 0.0,
                             baselineCells: Option[Seq[BaselineCell]] = None)

  case class BaselineCell(time: Double, cumHazard: Double)

}
