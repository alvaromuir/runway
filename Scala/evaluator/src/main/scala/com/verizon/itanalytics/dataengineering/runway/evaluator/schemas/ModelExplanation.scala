package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 24, 2018
 */

trait ModelExplanation extends Extension with Array {
  case class ModelExplanation(
      extension: Option[Seq[Extension]] = None,
      correlations: Option[Correlations],
      predictiveModelQualities: Option[Seq[PredictiveModelQuality]],
      clusteringModelQualities: Option[Seq[ClusteringModelQuality]]
  )

  case class Correlations(
      extension: Option[Seq[Extension]] = None,
      correlationFields: Array,
      correlationValues: Matrix,
      correlationMethods: Option[Matrix]
  )

  case class PredictiveModelQuality(
      extension: Option[Seq[Extension]] = None,
      confusionMatrix: Option[ConfusionMatrix] = None,
      liftData: Option[Seq[LiftData]] = None,
      ROC: Option[ROC] = None,
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
      AICc: Option[Double] = None
  )

  case class ConfusionMatrix(extension: Option[Seq[Extension]] = None,
                             classLabels: Option[ClassLabels] = None,
                             matrix: Option[Matrix] = None)

  case class ClassLabels(extension: Option[Seq[Extension]] = None,
                         labels: Array)

  case class Matrix(extension: Option[Seq[Extension]] = None,
                    matCell: Option[Seq[MatCell]] = None,
                    kind: String = "any",
                    nbRows: Option[Int] = None,
                    nbCols: Option[Int] = None,
                    diagDefault: Option[Double] = None,
                    offDiagDefault: Option[Double] = None)

  case class MatCell(row: Int, col: Int, value: String)

  case class LiftData(targetFieldValue: Option[String] = None,
                      targetFieldDisplayValue: Option[String] = None,
                      rankingQuality: Option[Double] = None,
                      modelLiftGraph: LiftGraph,
                      optimumLiftGraph: Option[LiftGraph] = None,
                      randomLiftGraph: Option[LiftGraph] = None)

  case class LiftGraph(extension: Option[Seq[Extension]] = None,
                       xCoordinates: Array,
                       yCoordinates: Array,
                       boundaryValues: Option[Array] = None,
                       boundaryValueMeans: Option[Array] = None)

  case class ROC(extension: Option[Seq[Extension]] = None,
                 positiveTargetFieldValue: String,
                 positiveTargetFieldDisplayValue: Option[String] = None,
                 negativeTargetFieldValue: Option[String] = None,
                 negativeTargetFieldDisplayValue: Option[String] = None,
                 rocGraph: Option[ROCGraph] = None)

  case class ROCGraph(extension: Option[Seq[Extension]] = None,
                      xCoordinates: Array,
                      yCoordinates: Array,
                      boundaryValues: Option[Array] = None)

  case class ClusteringModelQuality(dataName: Option[String] = None,
                                    SSE: Option[Double] = None,
                                    SSB: Option[Double] = None)

}
