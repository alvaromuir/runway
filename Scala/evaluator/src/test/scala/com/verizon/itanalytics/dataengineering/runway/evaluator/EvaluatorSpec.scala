package com.verizon.itanalytics.dataengineering.runway.evaluator

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
  "the evaluator" should
    "read create appropriate arguments for association models" in {
    val testModelPath = mapModels("association")

    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val inputField = evaluator.getInputFields.get(0).getName.getValue
    val observations = List("beer", "softdrink")

    val arguments = createArguments(pmmlModel, Map(inputField -> observations))

    assert(arguments.asScala.head._1.toString.contains(inputField))
    assert(arguments.asScala.head._2.toString.contains(observations.head))
  }

}
