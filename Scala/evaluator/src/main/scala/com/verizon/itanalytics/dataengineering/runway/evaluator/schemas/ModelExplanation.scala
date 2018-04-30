package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/


trait ModelExplanation {
  case class ModelExplanation(
      correlations: Option[Correlations],
      predictiveModelQuality: Option[PredictiveModelQuality],
      clusteringModelQuality: Option[ClusteringModelQuality]
  )

  case class Correlations(
      correlationFields: CorrelationFields,
      correlationValues: Matrix,
      correlationMethods: Option[Matrix]
  )

  case class CorrelationFields(n: String, `type`: String, value: String)

  case class PredictiveModelQuality(
      targetField: String,
      dataName: Option[String] = None,
      dataUsage: String = "training",
      meanError: Option[Double],
      meanAbsoluteError: Option[Double] = None,
      meanSquaredError: Option[Double] = None,
      rootMeanSquaredError: Option[Double] = None,
      `r-squared`: Option[Double] = None,
      `adj-r-squared`: Option[Double] = None,
      sumSquaredError: Option[Double] = None,
      sumSquaredRegression: Option[Double] = None,
      numOfRecords: Option[Double] = None,
      numOfRecordsWeighted: Option[Double] = None,
      numOfPredictors: Option[Double] = None,
      degreesOfFreedom: Option[Double] = None,
      fStatistic: Option[Double] = None,
      AIC: Option[Double] = None,
      BIC: Option[Double] = None,
      AICc: Option[Double] = None,
      confusionMatrix: Option[ConfusionMatrix] = None,
      liftData: Option[LiftData] = None,
      ROC: Option[ROC] = None
  )

  case class ConfusionMatrix(classLabels: Option[Seq[String]] = None,
                             matrix: Option[Matrix] = None)

  case class Matrix(kind: String = "any",
                    nbRows: Option[Int] = None,
                    nbCols: Option[Int] = None,
                    diagDefault: Option[Double] = None,
                    offDiagDefault: Option[Double] = None,
                    matCells: Option[Seq[MatCell]] = None)

  case class MatCell(
      row: Int,
      col: Int,
      value: String
  )

  case class LiftData(targetFieldValue: Option[String] = None,
                      targetFieldDisplayValue: Option[String] = None,
                      rankingQuality: Option[Double] = None,
                      modelLiftGraph: ModelLiftGraph,
                      optimumLiftGraph: Option[OptimumLiftGraph] = None,
                      randomLiftGraph: Option[RandomLiftGraph] = None)

  case class ModelLiftGraph(liftGraph: LiftGraph)

  case class OptimumLiftGraph(liftGraph: LiftGraph)

  case class RandomLiftGraph(liftGraph: LiftGraph)

  case class LiftGraph(xCoordinates: Seq[Double],
                       yCoordinates: Seq[Double],
                       boundaryValues: Option[Seq[Double]] = None,
                       boundaryValueMeans: Option[Seq[Double]] = None)

  case class ROC(positiveTargetFieldValue: String,
                 positiveTargetFieldDisplayValue: Option[String] = None,
                 negativeTargetFieldValue: Option[String] = None,
                 negativeTargetFieldDisplayValue: Option[String] = None,
                 rocGraph: Option[ROCGraph] = None)

  case class ROCGraph(
      yCoordinates: Seq[Double],
      boundaryValues: Option[Seq[Double]] = None,
      boundaryValueMeans: Option[Seq[Double]] = None
  )

  case class ClusteringModelQuality(
      dataName: Option[String] = None,
      SSE: Option[Double] = None,
      SSB: Option[Double] = None
  )

}
