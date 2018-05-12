package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import scala.collection.JavaConverters._
import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{FieldName, Model, PMML}
import org.jpmml.evaluator.{ModelEvaluator, UnsupportedElementException}
import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 12, 2018
*/


class SequenceRuleSpec
  extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("sequenceRule")
  val testDataPath = mapData("sequenceRule")

  "the evaluator" should
    "throw an UnsupportedElementException if trying to load a sequence rule model" in {
    val pMML: PMML = readPMML(new File(testModelPath))

    intercept[UnsupportedElementException] {
      evaluatePmml(pMML)
    }
  }

}
