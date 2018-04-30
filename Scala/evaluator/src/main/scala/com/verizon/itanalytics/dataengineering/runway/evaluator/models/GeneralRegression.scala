package com.verizon.itanalytics.dataengineering.runway.evaluator.models

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 27, 2018
*/


import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/GeneralRegression.html
trait GeneralRegression
    extends MiningSchema
    with Output
    with Statistics
    with ModelExplanation
    with Target
    with TransformationDictionary
    with ModelVerification {

  case class GeneralRegression(
      modelName: Option[String] = None,
      modelType: String,
      targetVariableName: String,
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
      startTimeVariable: Option[String] = None,
      endTimeVariable: Option[String] = None,
      subjectIDVariable: Option[String] = None,
      statusVariable: Option[String] = None,
      baselineStrataVariable: Option[String] = None,
      isScorable: Option[Boolean],
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Target]] = None,
      localTransformation: Option[LocalTransformation] = None,
      parameterList: Option[Seq[Parameter]],
      factorsList: Option[Seq[Predictor]] = None,
      covariateList: Option[Seq[Predictor]] = None,
      pPMatrix: Option[Seq[PPCell]] = None,
      pCovMatrix: Option[PCovMatrix] = None,
      paramMatrix: Option[Seq[PCell]] = None,
      eventValues: Option[EventValues] = None,
      baseCumHazardTables: Option[BaseCumHazardTable] = None,
      modelVerification: Option[ModelVerification] = None
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

  case class BaseCumHazardTable(
      maxTime: Option[Double] = None,
      baselineStratum: Option[Seq[BaselineStratum]] = None,
      baselineCells: Option[Seq[BaselineCell]] = None)

  case class BaselineStratum(value: String,
                             label: Option[String] = None,
                             maxTime: Double = 0.0,
                             baselineCells: Option[Seq[BaselineCell]] = None)

  case class BaselineCell(time: Double, cumHazard: Double)

}
