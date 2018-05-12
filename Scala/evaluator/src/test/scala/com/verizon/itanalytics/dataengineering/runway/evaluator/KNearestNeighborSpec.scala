package com.verizon.itanalytics.dataengineering.runway.evaluator

import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 05, 2018
*/

class KNearestNeighborSpec
  extends FlatSpec
    with Builder
    with Evaluator {

  //  val testModelPath = mapModels("nearestNeighbor")
  //  val testDataPath = mapData("nearestNeighbor")
  //  val pMML: PMML = readPMML(new File(testModelPath))
  //  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  //  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  //  val model: Option[BayesianNetworkModel] = pmmlModel.bayesianNetworkModel

  "the evaluator" should
    "read Nearest neighbor models" in {
    val status = "not yet implemented, requires a test file"

    assert(status != null)
    //    val modelClass = "Nearest neighbor model"
    //    assert(evaluator.getSummary == modelClass)
  }
}