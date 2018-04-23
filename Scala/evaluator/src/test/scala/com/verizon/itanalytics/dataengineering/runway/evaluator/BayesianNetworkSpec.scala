package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.{ModelEvaluator, UnsupportedElementException}
import org.scalatest.FlatSpec

class BayesianNetworkSpec
    extends FlatSpec
    with Builder
    with testutils.Utils
    with Evaluator {

  val testModelPath = mapModels("bayesianNetwork")

  "the evaluator" should
    "read Bayesian Network models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "regression"

    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
  }

  it should "throw an UnsupportedElementException if trying to load a bayesian network model" in {
    val pMML: PMML = readPMML(new File(testModelPath))

    intercept[UnsupportedElementException] {
      val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    }
  }

}
