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
* 05 14, 2018
*/


class TreesSpec
  extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("trees")
  val testDataPath = mapData("trees")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[TreeModel] = pmmlModel.treeModel

  "the evaluator" should
    "read Tree models" in {
    val modelClass = "Tree model"

    assert(evaluator.getSummary == modelClass)
  }

  it should "provide information on required input fields" in {
    val dataDictionary = pmmlModel.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(10))
    assert(taxonomies.isEmpty)
    assert(dataFields.size.equals(10))

    assert(dataFields.head.name.contains("Age"))
    assert(dataFields.head.displayName.isEmpty)
    assert(dataFields.head.optype.contains("continuous"))
    assert(dataFields.head.dataType.contains("integer"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.contains("0"))
    assert(dataFields.head.intervals.size.equals(1))
    assert(dataFields.head.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.head.intervals.get.head.leftMargin.contains(17.0))
    assert(dataFields.head.intervals.get.head.rightMargin.contains(90.0))
  }

  it should "identify as a Tree model in the PMML Schema" in {
    assert(Some(model).isDefined)
  }

  it should "contain the appropriate key-value pairs" in {
    assert(Some(model.get.extension).isDefined)
    assert(Some(model.get.miningSchema).isDefined)
    assert(Some(model.get.output).isDefined)
    assert(Some(model.get.modelStats).isDefined)
    assert(Some(model.get.modelExplanation).isDefined)
    assert(Some(model.get.targets).isDefined)
    assert(Some(model.get.localTransformations).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)
    assert(Some(model.get.isScorable).isDefined)
  }

  it should "return extension information, if available" in {
    assert(Some(model.get.extension).get.get.isEmpty)
  }

  it should "provide mining schema information, if available" in {
    val miningSchema = Some(model.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(10))
    assert(miningFields.head.name.contains("Age"))
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

  it should "provide local transformation information, if available" in {
    assert(model.get.localTransformations.isEmpty)
  }

  it should "provide Tree Model information on nodes" in {
    val node = model.get.node

    assert(node.predicate.contains("True"))
    assert(node.partition.isEmpty)
    assert(node.scoreDistributions.get.size.equals(2))
    assert(node.scoreDistributions.get.head.value.contains("0"))
    assert(node.scoreDistributions.get.head.recordCount.equals(1537.0))
    assert(node.scoreDistributions.get.head.confidence.isEmpty)
    assert(node.scoreDistributions.get.head.probability.isEmpty)
    assert(node.nodes.get.size.equals(6))
    assert(node.nodes.get.head.predicate.contains("SimplePredicate"))
    assert(node.nodes.get.head.partition.isEmpty)
    assert(node.nodes.get.head.scoreDistributions.get.size.equals(2))
    assert(node.nodes.get.head.nodes.get.isEmpty)
    assert(node.nodes.get.head.embeddedModel.isEmpty)
    assert(node.nodes.get.head.id.get.contains("1"))
    assert(node.nodes.get.head.score.get.contains("0"))
    assert(node.nodes.get.head.recordCount.get.equals(67.0))
    assert(node.nodes.get.head.defaultChild.isEmpty)
    assert(node.embeddedModel.isEmpty)
    assert(node.id.get.contains("0"))
    assert(node.score.get.contains("0"))
    assert(node.recordCount.get.equals(2000.0))
    assert(node.defaultChild.isEmpty)
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).contains(Some("DecisionTree")))
    assert(Some(model.get.functionName).contains("classification"))
    assert(Some(model.get.algorithmName).get.isEmpty)
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
  }

  it should "provide information on the target fields if available" in {
    val targets = evaluator.getTargetFields

    assert(Some(targets.get(0).getName).get.getValue.contains("TARGET_Adjusted"))
    assert(Some(targets.get(0).getOpType).get.value().contains("categorical"))
    assert(Some(targets.get(0).getDataType).get.value().contains("string"))
    assert(Some(targets.get(0).getCategories).get.size.equals(2))
    assert(Some(targets.get(0).getCategories).get.get(0).equals("0"))
    assert(Some(targets.get(0).getCategories).get.get(1).equals("1"))
    assert(Some(targets.get(0).getMiningField.getName).get.getValue.contains("TARGET_Adjusted"))
    assert(Some(targets.get(0).getMiningField.getUsageType).get.value().contains("predicted"))
    assert(Some(targets.get(0).getMiningField.getOpType).contains(null))
    assert(Some(targets.get(0).getMiningField.getImportance).contains(null))
    assert(Some(targets.get(0).getMiningField.getOutlierTreatment).get.value().contains("asIs"))
    assert(Some(targets.get(0).getMiningField.getLowValue).contains(null))
    assert(Some(targets.get(0).getMiningField.getHighValue).contains(null))
    assert(Some(targets.get(0).getMiningField.getMissingValueReplacement).contains(null))
    assert(Some(targets.get(0).getMiningField.getMissingValueTreatment).contains(null))
    assert(Some(targets.get(0).getMiningField.getInvalidValueReplacement).contains(null))
    assert(Some(targets.get(0).getTarget).contains(null))
  }

  it should "return results from observation inputs" in {
    assert(evaluator.verify().equals(())) // is empty

    val inputFields = evaluator.getInputFields.asScala.map {
      _.getName.getValue
    }.toSet[Any]

    val testData = readDataFile(new File(testDataPath), lineNum = 10).head
    val observations = testData.filterKeys(inputFields)

    val arguments = createArguments(pmmlModel, observations)
    val results = evaluator.evaluate(arguments)

    val field = FieldName.create("TARGET_Adjusted")
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("{result=0, probability_entries=[0=0.7685, 1=0.2315], entityId=0, confidence_entries=[]}"))
  }

}
