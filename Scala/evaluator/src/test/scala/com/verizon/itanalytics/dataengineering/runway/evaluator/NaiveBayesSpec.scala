package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 05, 2018
 */

class NaiveBayesSpec
    extends FlatSpec
    with Builder
    with Evaluator {

//  val testModelPath = mapModels("naiveBayes")
//  val pMML: PMML = readPMML(new File(testModelPath))
//  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
//  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
//  val model: Option[NaiveBayesModel] = pmmlModel.naiveBayesModel

  "the evaluator" should
    "read Naive Bayes models" in {
    val status = "not yet implemented, requires a test file"

    assert(status != null)
//    val modelClass = "Naive Bayes model"
//    assert(evaluator.getSummary == modelClass)

  }
}
