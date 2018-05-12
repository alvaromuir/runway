package com.verizon.itanalytics.dataengineering.runway.evaluator

import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 25, 2018
*/

class BaselineModelSpec
  extends FlatSpec
    with Builder
    with Evaluator {

  //  val testModelPath = mapModels("baseline")
  //  val testDataPath = mapData("baseline")
  //  val pMML: PMML = readPMML(new File(testModelPath))
  //  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  //  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  //  val model: Option[BayesianNetworkModel] = pmmlModel.bayesianNetworkModel

  "the evaluator" should
    "read Baseline models" in {
    val status = "not yet implemented, requires a test file"

    assert(status != null)
    //    val modelClass = "Baseline network model"
    //    assert(evaluator.getSummary == modelClass)
  }

}
