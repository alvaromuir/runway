package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.{ModelEvaluator, UnsupportedElementException}
import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 12, 2018
*/


class ScoreCardSpec
  extends FlatSpec
    with Builder
    with Evaluator {


//  val testModelPath = mapModels("scoreCard")
//  val testDataPath = mapData("scoreCard")
//  val pMML: PMML = readPMML(new File(testModelPath))
//  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
//  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
//  val model: Option[ScoreCardModel] = pmmlModel.scoreCardModel

  "the evaluator" should
    "read Scorecard models" in {
    val status = "not yet implemented, requires a test file"

    assert(status != null)
    //    val modelClass = "Scorecard model"
    //    assert(evaluator.getSummary == modelClass)
  }
}

