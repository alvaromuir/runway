package com.verizon.itanalytics.dataengineering.runway.evaluator

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 25, 2018
*/

import java.io.File

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class AssociationRulesSpec extends FlatSpec with Builder with TestUtils with Evaluator {

  val testModelPath = mapModels("association")

  "the evaluator" should
    "read Association models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "associationRules"

    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
  }

  it should "provide information on required input fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val dataDictionary = pmmlModel.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(2))
    assert(taxonomies.isEmpty)

    assert(dataFields.size.equals(2))
    assert(dataFields.head.name.contains("transaction"))
    assert(dataFields.head.displayName.isEmpty)
    assert(dataFields.head.optype.contains("categorical"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.contains("0"))
    assert(dataFields.head.intervals.get.isEmpty)

    assert(dataFields.tail.head.name.contains("item"))
    assert(dataFields.tail.head.displayName.isEmpty)
    assert(dataFields.tail.head.optype.contains("categorical"))
    assert(dataFields.tail.head.taxonomy.isEmpty)
    assert(dataFields.tail.head.isCyclic.get.contains("0"))
    assert(dataFields.tail.head.intervals.get.isEmpty)
  }

  it should "identified as an Association Rules model in the PMML Schema" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel).isDefined)
  }

  it should "have the appropriate key-value pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.modelName).isDefined)
    assert(Some(pmmlModel.associationModel.get.functionName).isDefined)
    assert(Some(pmmlModel.associationModel.get.algorithmName).isDefined)
    assert(Some(pmmlModel.associationModel.get.numberOfTransactions).isDefined)
    assert(Some(pmmlModel.associationModel.get.maxNumberOfItemsPerTA).isDefined)
    assert(Some(pmmlModel.associationModel.get.avgNumberOfItemsPerTA).isDefined)
    assert(Some(pmmlModel.associationModel.get.minimumSupport).isDefined)
    assert(Some(pmmlModel.associationModel.get.minimumConfidence).isDefined)
    assert(Some(pmmlModel.associationModel.get.lengthLimit).isDefined)
    assert(Some(pmmlModel.associationModel.get.numberOfItems).isDefined)
    assert(Some(pmmlModel.associationModel.get.numberOfItemsets).isDefined)
    assert(Some(pmmlModel.associationModel.get.numberOfRules).isDefined)
    assert(Some(pmmlModel.associationModel.get.isScorable).isDefined)
    assert(Some(pmmlModel.associationModel.get.output).isDefined)
    assert(Some(pmmlModel.associationModel.get.modelStats).isDefined)
    assert(Some(pmmlModel.associationModel.get.localTransformation).isDefined)
    assert(Some(pmmlModel.associationModel.get.miningSchema).isDefined)
    assert(Some(pmmlModel.associationModel.get.items).isDefined)
    assert(Some(pmmlModel.associationModel.get.itemSets).isDefined)
    assert(Some(pmmlModel.associationModel.get.associationRules).isDefined)
    assert(Some(pmmlModel.associationModel.get.modelVerification).isDefined)
  }

  it should "have basic string-based fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.modelName).get.isEmpty)
    assert(Some(pmmlModel.associationModel.get.functionName).contains("associationRules"))
    assert(Some(pmmlModel.associationModel.get.algorithmName).get.isEmpty)
    assert(Some(pmmlModel.associationModel.get.isScorable).get.contains(true))
  }

  it should "have basic numeric-based fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.numberOfTransactions).contains(100))
    assert(Some(pmmlModel.associationModel.get.maxNumberOfItemsPerTA).get.isEmpty)
    assert(Some(pmmlModel.associationModel.get.avgNumberOfItemsPerTA).get.isEmpty)
    assert(Some(pmmlModel.associationModel.get.minimumSupport).contains(5.0E-4))
    assert(Some(pmmlModel.associationModel.get.minimumConfidence).contains(0.8))
    assert(Some(pmmlModel.associationModel.get.lengthLimit).get.isEmpty)
    assert(Some(pmmlModel.associationModel.get.numberOfItems).contains(11))
    assert(Some(pmmlModel.associationModel.get.numberOfItemsets).contains(357))
    assert(Some(pmmlModel.associationModel.get.numberOfRules).contains(770))
  }

  it should "have schema information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val miningSchema = Some(pmmlModel.associationModel.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(2))
    assert(miningFields.head.name.contains("transaction"))
    assert(miningFields.head.usageType.contains("group"))
    assert(miningFields.head.optype.isEmpty)
    assert(miningFields.head.importance.isEmpty)
    assert(miningFields.head.outliers.contains("asIs"))
    assert(miningFields.head.lowValue.isEmpty)
    assert(miningFields.head.highValue.isEmpty)
    assert(miningFields.head.missingValueReplacement.isEmpty)
    assert(miningFields.head.missingValueTreatment.isEmpty)
    assert(miningFields.head.invalidValueTreatment.contains("returnInvalid"))
  }

  it should "have output information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.output).get.isEmpty)
  }

  it should "have statistics information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.modelStats).get.isEmpty)
  }

  it should "have local transformation information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.localTransformation).get.isEmpty)
  }

  it should "have information on the models verification" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.associationModel.get.modelVerification).get.isEmpty)
  }

  it should "have details on items" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val items = pmmlModel.associationModel.get.items
    assert(items.get.size.equals(Some(pmmlModel.associationModel.get.numberOfItems).get))
    assert(items.get.head.id.contains("1"))
    assert(items.get.head.value.contains("beer"))
    assert(items.get.head.field.isEmpty)
    assert(items.get.head.category.isEmpty)
    assert(items.get.head.mappedValue.isEmpty)
    assert(items.get.head.weight.isEmpty)
  }

  it should "have details on item sets" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val itemSets = pmmlModel.associationModel.get.itemSets

    assert(itemSets.get.size.equals(Some(pmmlModel.associationModel.get.numberOfItemsets).get))
    assert(itemSets.get.head.id.contains("1"))
    assert(itemSets.get.head.support.isEmpty)
    assert(itemSets.get.head.numberOfItems.contains(2))
    assert(itemSets.get.head.itemRefs.size.equals(2))
    assert(itemSets.get.head.itemRefs.toList.head.itemRef.equals("5"))
  }

  it should "have details the association rules" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val associationRules = pmmlModel.associationModel.get.associationRules

    assert(associationRules.get.size.equals(Some(pmmlModel.associationModel.get.numberOfRules).get))
    assert(associationRules.get.head.antecedent.contains("1"))
    assert(associationRules.get.head.consequent.contains("348"))
    assert(associationRules.get.head.support.equals(0.02))
    assert(associationRules.get.head.confidence.equals(1.0))
    assert(associationRules.get.head.lift.contains(2.63157894736842))
    assert(associationRules.get.head.leverage.isEmpty)
    assert(associationRules.get.head.affinity.isEmpty)
    assert(associationRules.get.head.id.isEmpty)
  }

  it should "return results from observation inputs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(evaluator.verify().equals(())) // is empty

    val inputField = evaluator.getInputFields.get(0).getName.getValue
    val observations = Map(inputField -> List("beer", "softdrink")).toMap[Any, Any]

    val arguments = createArguments(pmmlModel, observations)

    val results = evaluator.evaluate(arguments)
    assert(Some(results.asScala.keys.head).contains(null))
    assert(results.asScala(null).toString.contains("antecedentFlags={1, 2, 3}"))
  }

}
