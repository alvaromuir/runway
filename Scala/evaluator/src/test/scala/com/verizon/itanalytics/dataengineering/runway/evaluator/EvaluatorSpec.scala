package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/

class EvaluatorSpec
  extends FlatSpec
    with Builder
    with Evaluator {

  val testModelPath = mapModels("association")
  var pMML: PMML = readPMML(new File(testModelPath))
  var evaluator: ModelEvaluator[_ <: Model] = _
  var pmmlSchema: PMMLSchema = _

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
    pmmlSchema = parsePmml(evaluator.getPMML)
    assert(pmmlSchema.associationModel.isDefined)
  }

  it should "parses observations appropriately for arguments" in {
    val inputField = evaluator.getInputFields.get(0).getName.getValue
    val observations = List("beer", "softdrink")

    val arguments = createArguments(pmmlSchema, Map(inputField -> observations))

    assert(arguments.asScala.head._1.toString.contains(inputField))
    assert(arguments.asScala.head._2.toString.contains(observations.head))
  }
}
