package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 25, 2018
 */

class AssociationRulesSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("association")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[AssociationModel] = pmmlModel.associationModel

  "the evaluator" should
    "read Association models" in {
    val modelClass = "Association rules"

    assert(evaluator.getSummary == modelClass)
  }

  it should "provide information on required input fields" in {
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
  }

  it should "identify as an Association Rules Model in the PMML Schema" in {
    assert(model.isDefined)
  }

  it should "contain the appropriate key-value pairs" in {
    assert(Some(model.get.extension).isDefined)
    assert(Some(model.get.miningSchema).isDefined)
    assert(Some(model.get.output).isDefined)
    assert(Some(model.get.modelStats).isDefined)
    assert(Some(model.get.localTransformations).isDefined)
    assert(Some(model.get.item).isDefined)
    assert(Some(model.get.itemSet).isDefined)
    assert(Some(model.get.associationRule).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)
    assert(Some(model.get.numberOfTransactions).isDefined)
    assert(Some(model.get.maxNumberOfItemsPerTA).isDefined)
    assert(Some(model.get.avgNumberOfItemsPerTA).isDefined)
    assert(Some(model.get.minimumSupport).isDefined)
    assert(Some(model.get.minimumConfidence).isDefined)
    assert(Some(model.get.lengthLimit).isDefined)
    assert(Some(model.get.numberOfItems).isDefined)
    assert(Some(model.get.numberOfItemsets).isDefined)
    assert(Some(model.get.numberOfRules).isDefined)
    assert(Some(model.get.isScorable).isDefined)
  }

  it should "return extension information, if available" in {
    assert(Some(model.get.extension).get.get.isEmpty)
  }

  it should "provide mining schema information, if available" in {
    val miningSchema = Some(model.get.miningSchema)
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

  it should "provide output information, if available" in {
    assert(Some(model.get.output).get.isEmpty)
  }

  it should "provide model statistics information, if available" in {
    assert(Some(model.get.modelStats).get.isEmpty)
  }

  it should "provide local transformation information, if available" in {
    assert(Some(model.get.localTransformations).get.isEmpty)
  }

  it should "provide Association Rules Model item information" in {
    val items = model.get.item

    assert(items.get.size.equals(Some(model.get.numberOfItems).get))
    assert(items.get.head.id.contains("1"))
    assert(items.get.head.value.contains("beer"))
    assert(items.get.head.field.isEmpty)
    assert(items.get.head.category.isEmpty)
    assert(items.get.head.mappedValue.isEmpty)
    assert(items.get.head.weight.isEmpty)
  }

  it should "provide Association Rules Model item sets information" in {
    val itemSets = model.get.itemSet

    assert(itemSets.get.size.equals(Some(model.get.numberOfItemsets).get))
    assert(itemSets.get.head.id.contains("1"))
    assert(itemSets.get.head.support.isEmpty)
    assert(itemSets.get.head.numberOfItems.contains(2))
    assert(itemSets.get.head.itemRefs.size.equals(2))
    assert(itemSets.get.head.itemRefs.toList.head.itemRef.equals("5"))
  }

  it should "provide details the Association Rules Models" in {
    val associationRules = model.get.associationRule

    assert(associationRules.get.size.equals(Some(model.get.numberOfRules).get))
    assert(associationRules.get.head.antecedent.contains("1"))
    assert(associationRules.get.head.consequent.contains("348"))
    assert(associationRules.get.head.support.equals(0.02))
    assert(associationRules.get.head.confidence.equals(1.0))
    assert(associationRules.get.head.lift.contains(2.63157894736842))
    assert(associationRules.get.head.leverage.isEmpty)
    assert(associationRules.get.head.affinity.isEmpty)
    assert(associationRules.get.head.id.isEmpty)
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).get.isEmpty)
    assert(Some(model.get.functionName).contains("associationRules"))
    assert(Some(model.get.algorithmName).get.isEmpty)
  }

  it should "return model-specific numeric-based fields" in {
    assert(Some(model.get.numberOfTransactions).contains(100))
    assert(Some(model.get.maxNumberOfItemsPerTA).get.isEmpty)
    assert(Some(model.get.avgNumberOfItemsPerTA).get.isEmpty)
    assert(Some(model.get.minimumSupport).contains(5.0E-4))
    assert(Some(model.get.minimumConfidence).contains(0.8))
    assert(Some(model.get.lengthLimit).get.isEmpty)
    assert(Some(model.get.numberOfItems).contains(11))
    assert(Some(model.get.numberOfItemsets).contains(357))
    assert(Some(model.get.numberOfRules).contains(770))
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
  }

  it should "return results from observation inputs" in {
    assert(evaluator.verify().equals(())) // is empty

    val inputField = evaluator.getInputFields.get(0).getName.getValue

    val observations = Map(inputField -> List("beer", "softdrink")).toMap[Any, Any]

    val arguments = createArguments(pmmlModel, observations)
    val results = evaluator.evaluate(arguments)

    val field = null
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("antecedentFlags={1, 2, 3}"))
  }

}
