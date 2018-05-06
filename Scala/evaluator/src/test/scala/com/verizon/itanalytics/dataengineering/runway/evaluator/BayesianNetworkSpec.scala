package com.verizon.itanalytics.dataengineering.runway.evaluator

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 26, 2018
*/

import java.io.File

import org.dmg.pmml.PMML
import org.jpmml.evaluator.UnsupportedElementException
import org.scalatest.FlatSpec

class BayesianNetworkSpec
    extends FlatSpec
    with Builder
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
      evaluatePmml(pMML)
    }
  }

}
