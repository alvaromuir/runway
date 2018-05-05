package com.verizon.itanalytics.dataengineering.runway.evaluator
/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/

import java.io.File

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class EvaluatorSpec
  extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("association")
  var pMML: PMML = readPMML(new File(testModelPath))
  var evaluator: ModelEvaluator[_ <: Model] = null
  var pmmlModel: PMMLSchema = null

  "the evaluator" should
    "read files and return a valid PMML instance" in {
    pMML = readPMML(new File(testModelPath))
    assert(pMML.isInstanceOf[org.dmg.pmml.PMML])
  }

  it should "evaluate files into valid Model Evaluator type" in {
    evaluator = evaluatePmml(pMML)
    assert(evaluator.getModel.getMiningFunction.value.contains("associationRules"))
  }

  it should "parse files into valid PMMLSchema" in {
    pmmlModel = parsePmml(evaluator.getPMML)
    assert(pmmlModel.associationModel.isDefined)
  }

  it should "parses observations appropriately for arguments" in {
    val inputField = evaluator.getInputFields.get(0).getName.getValue
    val observations = List("beer", "softdrink")

    val arguments = createArguments(pmmlModel, Map(inputField -> observations))

    assert(arguments.asScala.head._1.toString.contains(inputField))
    assert(arguments.asScala.head._2.toString.contains(observations.head))
  }
}
