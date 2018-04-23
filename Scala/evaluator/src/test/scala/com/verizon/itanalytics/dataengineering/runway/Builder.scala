package com.verizon.itanalytics.dataengineering.runway

import org.scalatest.{BeforeAndAfterEach, Suite}

trait Builder extends BeforeAndAfterEach {
  this: Suite =>
  val srcDirPath = "src/test/resources/"
  val mapModels = Map("association" -> s"$srcDirPath/ShoppingAssocRules.xml",
                      "bayesianNetwork" -> s"$srcDirPath/BayesianTest.xml")

  override def beforeEach() {
    super.beforeEach()
  }

  override def afterEach() {
    try super.afterEach()
    catch { case e: Exception => throw e }
  }
}
