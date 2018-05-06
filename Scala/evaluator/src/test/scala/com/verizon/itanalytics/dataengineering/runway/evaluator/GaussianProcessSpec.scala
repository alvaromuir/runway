package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import org.dmg.pmml.PMML
import org.jpmml.evaluator.UnsupportedElementException
import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 27, 2018
*/

class GaussianProcessSpec
    extends FlatSpec
    with Builder
    with Evaluator {

  val testModelPath = mapModels("gaussian")
  val testDataPath = mapData("gaussian")
  val pMML: PMML = readPMML(new File(testModelPath))
  val miningFunction = "regression"

  "the evaluator" should
    "read Gaussian Process models" in {


    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
  }

  it should "throw an UnsupportedElementException if trying to load a gaussian process model" in {
    val pMML: PMML = readPMML(new File(testModelPath))

    intercept[UnsupportedElementException] {
      evaluatePmml(pMML)
    }
  }
}
