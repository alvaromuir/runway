package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.{ModelEvaluator, UnsupportedElementException}
import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 26, 2018
*/

class BayesianNetworkSpec
    extends FlatSpec
    with Builder
    with Evaluator {

//  val testModelPath = mapModels("bayesianNetwork")
//  val testDataPath = mapData("bayesianNetwork")
//  val pMML: PMML = readPMML(new File(testModelPath))
//  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
//  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
//  val model: Option[BayesianNetworkModel] = pmmlModel.bayesianNetworkModel

  "the evaluator" should
    "read Bayesian Network models" in {
    val status = "not yet implemented, requires a test file"

    assert(status != null)
    //    val modelClass = "Bayesian network model"
    //    assert(evaluator.getSummary == modelClass)
  }

}
