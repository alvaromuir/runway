package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{FieldName, Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 06, 2018
*/


class NeuralNetworkSpec extends FlatSpec
  with Builder
  with TestUtils
  with Evaluator {

  val testModelPath = mapModels("neuralNetwork")
  val testDataPath = mapData("neuralNetwork")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[NeuralNetworkModel] = pmmlSchema.neuralNetwork

  "the evaluator" should
    "read Neural Network models" in {
    val modelClass = "Neural network"

    assert(evaluator.getSummary == modelClass)
  }

  it should "provide information on required input fields" in {
    val dataDictionary = pmmlSchema.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(5))
    assert(taxonomies.isEmpty)
    assert(dataFields.size.equals(5))

    assert(dataFields.head.name.contains("sepal_length"))
    assert(dataFields.head.displayName.isEmpty)
    assert(dataFields.head.optype.contains("continuous"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.equals("0"))
    assert(dataFields.head.intervals.size.equals(1))
    assert(dataFields.head.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.head.intervals.get.head.leftMargin.contains(4.3))
    assert(dataFields.head.intervals.get.head.rightMargin.contains(7.9))
  }

  it should "identify as an Neural Network model in the PMML Schema" in {
    assert(model.isDefined)
  }

  it should "contain the appropriate key-value pairs" in {
    assert(Some(model.get.extension).isDefined)
    assert(Some(model.get.miningSchema).isDefined)
    assert(Some(model.get.output).isDefined)
    assert(Some(model.get.modelStats).isDefined)
    assert(Some(model.get.modelExplanation).isDefined)
    assert(Some(model.get.targets).isDefined)
    assert(Some(model.get.localTransformations).isDefined)
    assert(Some(model.get.neuralInputs).isDefined)
    assert(Some(model.get.neuralOutputs).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)

    assert(Some(model.get.activationFunction).isDefined)
    assert(Some(model.get.normalizationMethod).isDefined)
    assert(Some(model.get.threshold).isDefined)
    assert(Some(model.get.numberOfHiddenLayers).isDefined)
    assert(Some(model.get.isScorable).isDefined)
  }

  it should "return extension information, if available" in {
    assert(Some(model.get.extension).get.get.isEmpty)
  }

  it should "provide mining schema information, if available" in {
    val miningSchema = Some(model.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(5))
    assert(miningFields.head.name.contains("sepal_length"))
    assert(miningFields.head.usageType.contains("active"))
    assert(miningFields.head.optype.isEmpty)
    assert(miningFields.head.importance.isEmpty)
    assert(miningFields.head.outliers.contains("asIs"))
    assert(miningFields.head.lowValue.isEmpty)
    assert(miningFields.head.highValue.isEmpty)
    assert(miningFields.head.missingValueReplacement.isEmpty)
    assert(miningFields.head.missingValueTreatment.isEmpty)
    assert(miningFields.head.invalidValueTreatment.contains("asIs"))
  }

  it should "provide output information, if available" in {
    assert(Some(model.get.output).get.isEmpty)
  }

  it should "provide model statistics information, if available" in {
    assert(Some(model.get.modelStats).get.isEmpty)
  }

  it should "provide model explanation, if available" in {
    assert(model.get.modelExplanation.isEmpty)
  }

  it should "provide model targets, if available" in {
    assert(model.get.targets.isEmpty)
  }

  it should "provide local transformation information, if available" in {
    val derivedFields = Some(
      model.get.localTransformations.get.derivedFields.head)

    assert(derivedFields.get.size.equals(4))
    assert(derivedFields.get.head.name.get.contains("sepal_length*"))
    assert(Some(derivedFields.get.head.displayName).contains(Some("sepal_length")))
    assert(derivedFields.get.head.optype.contains("continuous"))
    assert(derivedFields.get.head.dataType.contains("double"))
  }

  it should "provide Neural Network Model information on neural inputs" in {
    val inputs = model.get.neuralInputs
    val neuralInput = inputs.neuralInput.head
    val derivedField = neuralInput.derivedField

    assert(inputs.numberOfInputs.equals(4))
    assert(inputs.neuralInput.size.equals(4))

    assert(neuralInput.id.contains("0,0"))

    assert(derivedField.name.isEmpty)
    assert(derivedField.displayName.isEmpty)
    assert(derivedField.optype.contains("continuous"))
    assert(derivedField.dataType.contains("double"))
    assert(derivedField.intervals.get.isEmpty)
    assert(derivedField.values.get.isEmpty)
  }

  it should "provide Neural Network Model information on neural hidden layers" in {
    val hiddenLayers = model.get.neuralLayer
    val neuralLayer = hiddenLayers.head

    assert(hiddenLayers.size.equals(2))
    assert(neuralLayer.neurons.size.equals(3))
    assert(neuralLayer.activationFunction.isEmpty)
    assert(neuralLayer.threshold.isEmpty)
    assert(neuralLayer.altitude.isEmpty)
    assert(neuralLayer.normalizationMethod.isEmpty)

    assert(neuralLayer.neurons.head.id.equals("1,0"))
    assert(neuralLayer.neurons.head.altitude.isEmpty)
    assert(neuralLayer.neurons.head.width.isEmpty)
    assert(neuralLayer.neurons.head.bias.contains(40.4715596724959))

  }

  it should "provide Neural Network Model information on neural Outputs" in {
    val outputs = model.get.neuralOutputs
    val neuralOutput = outputs.neuralOutput.head
    val derivedField = neuralOutput.derivedField
    val outputNeuron = neuralOutput.outputNeuron

    assert(outputs.numberOfOutputs.equals(3))
    assert(outputs.neuralOutput.size.equals(3))

    assert(derivedField.name.isEmpty)
    assert(derivedField.displayName.isEmpty)
    assert(derivedField.optype.contains("categorical"))
    assert(derivedField.dataType.contains("string"))
    assert(derivedField.intervals.get.isEmpty)
    assert(derivedField.values.get.isEmpty)

    assert(outputNeuron.contains("2,0"))
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).get.isEmpty)
    assert(Some(model.get.functionName).contains("classification"))
    assert(Some(model.get.algorithmName).get.contains("RProp"))
    assert(Some(model.get.activationFunction).get.equals("logistic"))
    assert(Some(model.get.normalizationMethod).get.contains("none"))
  }

  it should "return model-specific numeric-based fields" in {
    assert(Some(model.get.threshold).contains(0.0))
    assert(Some(model.get.numberOfHiddenLayers).contains(2))
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
  }

  it should "provide information on the target fields if available" in {
    val targets = evaluator.getTargetFields

    assert(Some(targets.get(0).getName).get.getValue.contains("class"))
    assert(Some(targets.get(0).getDataType).get.value().contains("string"))
    assert(Some(targets.get(0).getOpType).get.value().contains("categorical"))
  }

  it should "return results from observation inputs" in {
    assert(evaluator.verify().equals(())) // is empty

    val inputFields = evaluator.getInputFields.asScala.map {
      _.getName.getValue
    }.toSet[Any]

    val testData = readDataFile(new File(testDataPath), lineNum = 10).head
    val observations = testData.filterKeys(inputFields)

    val arguments = createArguments(pmmlSchema, observations)
    val results = evaluator.evaluate(arguments)

    val field = FieldName.create("class")
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("result=Iris-setosa"))
    assert(Some(results.asScala(field)).get.toString.contains("entityId=2,0"))
  }

}
