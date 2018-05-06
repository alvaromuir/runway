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
    "bayesianNetwork" -> s"$modelsSrcPath/BayesianTest.xml",
    "clustering" -> s"$modelsSrcPath/single_audit_kmeans.xml",
    "gaussian" -> s"$modelsSrcPath/GaussianTest.xml",
    "generalRegression" -> s"$modelsSrcPath/IrisGeneralRegression.xml",
    "naiveBayes" -> s"$modelsSrcPath/NaiveBayesTest.xml"
  )

  val dataSrcPath = "./evaluator/src/test/resources/data"
  val mapData = Map(
    "association" -> s"$dataSrcPath/baskets1ntrans.csv",
    "bayesianNetwork" -> s"$dataSrcPath/ALARM10k.csv",
    "clustering" -> s"$dataSrcPath/Audit.csv",
    "gaussian" -> s"$dataSrcPath/GaussianTest.csv",
    "generalRegression" -> s"$dataSrcPath/Iris.csv"
  )

  override def beforeEach() {
    super.beforeEach()
  }

  override def afterEach() {
    try super.afterEach()
    catch { case e: Exception => throw e }
  }
}
