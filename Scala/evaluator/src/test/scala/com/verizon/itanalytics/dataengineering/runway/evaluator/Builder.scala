package com.verizon.itanalytics.dataengineering.runway.evaluator

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 25, 2018
*/

import org.scalatest.{BeforeAndAfterEach, Suite}

trait Builder extends BeforeAndAfterEach {
  this: Suite =>
  val modelsSrcPath = "./evaluator/src/test/resources/models"
  val mapModels = Map(
    "association" -> s"$modelsSrcPath/ShoppingAssocRules.xml",
    "baseline" -> s"",
    "bayesianNetwork" -> s"$modelsSrcPath/BayesianTest.xml",
    "clustering" -> s"$modelsSrcPath/single_audit_kmeans.xml",
    "gaussian" -> s"$modelsSrcPath/GaussianTest.xml",
    "generalRegression" -> s"$modelsSrcPath/IrisGeneralRegression.xml",
    "naiveBayes" -> s"$modelsSrcPath/NaiveBayesTest.xml",
    "nearestNeighbord"  -> s"",
    "neuralNetwork" -> s"$modelsSrcPath/single_iris_mlp.xml",
    "regression" -> s"$modelsSrcPath/ElNinoLinearReg.xml",
    "ruleSet" -> s"$modelsSrcPath/RuleSetCompoundTest.xml",
    "sequence" -> s"$modelsSrcPath/sequences_rule_model.xml",
    "supportVectorMachine" -> s"",
    "text" -> s"",
    "timeSeries" -> s"",
    "tree" -> s"",
    "scoreCard" -> s""
  )

  val dataSrcPath = "./evaluator/src/test/resources/data"
  val mapData = Map(
    "association" -> s"$dataSrcPath/baskets1ntrans.csv",
    "bayesianNetwork" -> s"$dataSrcPath/ALARM10k.csv",
    "baseline" -> s"",
    "clustering" -> s"$dataSrcPath/Audit.csv",
    "gaussian" -> s"$dataSrcPath/GaussianTest.csv",
    "generalRegression" -> s"$dataSrcPath/Iris.csv",
    "naiveBayes" -> s"",
    "nearestNeighbor"  -> s"",
    "neuralNetwork" -> s"$dataSrcPath/Iris.csv",
    "regression" -> s"$dataSrcPath/Elnino.csv",
    "ruleSet" -> s"$dataSrcPath/switzerland.data",
    "sequence" -> s"$dataSrcPath/Visits_small.csv",
    "supportVectorMachine" -> s"",
    "text" -> s"",
    "timeSeries" -> s"",
    "tree" -> s"",
    "scoreCard" -> s""
  )

  override def beforeEach() {
    super.beforeEach()
  }

  override def afterEach() {
    try super.afterEach()
    catch { case e: Exception => throw e }
  }
}
