package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File
import java.util

import org.dmg.pmml.DataType._
import org.dmg.pmml.OpType._
import org.dmg.pmml.PMML

import org.jpmml.evaluator.mining.MiningModelEvaluator
import org.jpmml.evaluator._

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.collection.JavaConversions._
import scala.language.postfixOps

import org.scalatest.{BeforeAndAfterEach, FlatSpec, Suite}

import com.verizon.itanalytics.dataengineering.runway.evaluator.Manager._

trait Builder extends BeforeAndAfterEach {
  this: Suite =>

  val filesBuffer = new ListBuffer[String]

  override def beforeEach() {
    val sourceDir = "./evaluator/src/test/resources"

    val pmmlFileName = "test.pmml"
    val pmmlFile = sourceDir + "/" + pmmlFileName
    filesBuffer.append(pmmlFile)

    val dataFileName = "test.csv"
    val dataFile = sourceDir + "/" + dataFileName
    filesBuffer.append(dataFile)

    super.beforeEach()
  }

  override def afterEach() {
    try super.afterEach()
    finally filesBuffer.clear()
  }
}


class ManagerSpec extends FlatSpec with Builder {

  "the test PMML descriptor file" should
    "be of version 4.3" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))

    assert(pMML.getBaseVersion === "4.3")
  }

  it should "have models embedded" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))

    assert(pMML.hasModels === true)
  }

  it should "have 5 fields" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val dataFields = pMML.getDataDictionary.getDataFields.map(x => x.getName)

    assert(pMML.getDataDictionary.getNumberOfFields == 5)
    assert(dataFields.head.toString === "Species")
    assert(dataFields(1).toString === "Sepal.Length")
    assert(dataFields(2).toString === "Sepal.Width")
    assert(dataFields(3).toString === "Petal.Length")
    assert(dataFields(4).toString === "Petal.Width")
  }

  "the test PMML data when scoring observations" should
    "have 5 fields that match the model, including the target variable" in {
    val data = Source.fromFile(filesBuffer(1))
    val fields = data.getLines.take(1).toList.head.split(",")
    data.close()

    assert(fields.length == 4)
    assert(fields(0) == "sepal_length")
    assert(fields(1) == "sepal_width")
    assert(fields(2) == "petal_length")
    assert(fields(3) == "petal_width")
  }

  "the evaluator factory when creating a new instance" should
    "confirm its input model" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val evaluator = getEvaluator(pMML)

    // The test file is a random forest
    assert(evaluator.getSummary === "Ensemble model")
  }

  it should "read the input pMML as 4 CONTINUOUS features of type DOUBLE" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val evaluator = getEvaluator(pMML)
    val inputFields:util.List[InputField] = evaluator.getInputFields

    assert(inputFields.size() === 4)

    assert(inputFields(0).getName.toString === "Sepal.Length")
    assert(inputFields(0).getDataType === DOUBLE)
    assert(inputFields(0).getOpType === CONTINUOUS)

    assert(inputFields(1).getName.toString === "Sepal.Width")
    assert(inputFields(1).getDataType === DOUBLE)
    assert(inputFields(1).getOpType === CONTINUOUS)

    assert(inputFields(2).getName.toString === "Petal.Length")
    assert(inputFields(2).getDataType === DOUBLE)
    assert(inputFields(2).getOpType === CONTINUOUS)

    assert(inputFields(3).getName.toString === "Petal.Width")
    assert(inputFields(3).getDataType === DOUBLE)
    assert(inputFields(3).getOpType === CONTINUOUS)
  }

  it should "read the target fields as 1 CATEGORICAL feature of type STRING" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val evaluator = getEvaluator(pMML)
    val targetFields:util.List[TargetField] = evaluator.getTargetFields

    // the tarvar is a CATEGORICAL type of STRING, 'Species'
    assert(targetFields.size() === 1)
    assert(targetFields.head.getDataType === STRING)
    assert(targetFields.head.getOpType === CATEGORICAL)
  }

  it should "return an output of 1 prediction and 3 probabilities" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val evaluator = getEvaluator(pMML)
    val outputFields:util.List[OutputField] = evaluator.getOutputFields

    // the outputs are Predicted_Species, Probability_setosa, Probability_versicolor, Probability_virginica
    assert(outputFields.size() === 4)

    assert(outputFields(0).getDataType === null)
    assert(outputFields(0).getOpType === null)
    assert(outputFields(0).isFinalResult === true)
    assert(outputFields(0).getDepth === 0)

    assert(outputFields(1).getDataType === DOUBLE)
    assert(outputFields(1).getOpType === CONTINUOUS)
    assert(outputFields(1).isFinalResult === true)
    assert(outputFields(1).getDepth === 0)

    assert(outputFields(2).getDataType === DOUBLE)
    assert(outputFields(2).getOpType === CONTINUOUS)
    assert(outputFields(2).isFinalResult === true)
    assert(outputFields(2).getDepth === 0)

    assert(outputFields(3).getDataType === DOUBLE)
    assert(outputFields(3).getOpType === CONTINUOUS)
    assert(outputFields(3).isFinalResult === true)
    assert(outputFields(3).getDepth === 0)
  }

  "the evaluator when evaluating models" should
    "match data input arguments with model arguments" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val evaluator = new MiningModelEvaluator(pMML)

    val data = Source.fromFile(filesBuffer(1))
    val line = data.getLines.slice(1, 2).next
    data.close()

    val lineVariables = line.split(",")
    val inputFields:util.List[InputField] = evaluator.getInputFields

    val arguments = getArguments(line, inputFields, evaluator)

    assert(arguments.size === inputFields.length)
    assert(arguments.size === lineVariables.length)

    for((_, idx) <- inputFields.zipWithIndex) {
      assert(arguments(inputFields(idx).getName).getDataType === inputFields(idx).getDataType)
      assert(arguments(inputFields(idx).getName).getOpType === inputFields(idx).getOpType)
    }
  }

  it should "return appropriate results from a scored data" in {
    val pMML: PMML = readPMML(new File(filesBuffer.head))
    val evaluator = new MiningModelEvaluator(pMML)

    val data = Source.fromFile(filesBuffer(1))
    val line = data.getLines.slice(1, 2).next
    data.close()

    val inputFields:util.List[InputField] = evaluator.getInputFields
    val arguments = getArguments(line, inputFields, evaluator)

    val results = evaluator.evaluate(arguments)
    val targetFields = evaluator.getTargetFields
    val outputFields = evaluator.getOutputFields

    for (targetField <- targetFields) {
      val targetFieldName = targetField.getName
      val targetFieldValue = results.get(targetFieldName)

      assert(targetFieldName.toString === "Species")
      assert(targetFieldValue.isInstanceOf[HasProbability])
    }

    assert(outputFields.map(x => x.getName.toString).head === s"Predicted_${targetFields.map(x => x.getName)head }" )
  }
}
