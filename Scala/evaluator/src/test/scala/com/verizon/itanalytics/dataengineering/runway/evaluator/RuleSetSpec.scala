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
 * 05 11, 2018
 */

class RuleSetSpec extends FlatSpec with Builder with TestUtils with Evaluator {

  val testModelPath = mapModels("ruleSet")
  val testDataPath = mapData("ruleSet")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[RuleSetModel] = pmmlModel.ruleSetModel

  "the evaluator" should
    "read Rule Set models" in {
    val modelClass = "Ruleset model"

    assert(evaluator.getSummary == modelClass)
  }

  it should "provide information on required input fields" in {
    val dataDictionary = pmmlModel.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(7))
    assert(taxonomies.isEmpty)
    assert(dataFields.size.equals(7))

    assert(dataFields.head.name.contains("BP"))
    assert(dataFields.head.displayName.contains("BP"))
    assert(dataFields.head.optype.contains("categorical"))
    assert(dataFields.head.dataType.contains("string"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.equals("0"))
    assert(dataFields.head.intervals.get.isEmpty)
  }

  it should "identify as a Rule set model in the PMML Schema" in {
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
    assert(Some(model.get.ruleSet).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)
    assert(Some(model.get.isScorable).isDefined)
  }

  it should "provide mining schema information, if available" in {
    val miningSchema = Some(model.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(7))
    assert(miningFields.head.name.contains("BP"))
    assert(miningFields.head.usageType.contains("active"))
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

  it should "provide model explanation, if available" in {
    assert(model.get.modelExplanation.isEmpty)
  }

  it should "provide model targets, if available" in {
    assert(model.get.targets.isEmpty)
  }

  it should "provide local transformation information, if available" in {
    assert(model.get.localTransformations.isEmpty)
  }

  it should "provide Rule set mode information on the rule set" in {
    val rules = model.get.ruleSet
    val rule = rules.rules.head.compoundRule.get.rules.head.compoundRule.get.rules.head.simpleRule.get

    assert(rules.scoreDistribution.get.isEmpty)
    assert(rules.rules.head.simpleRule.isEmpty)
    assert(rules.rules.head.compoundRule.get.rules.size.equals(2))
    assert(rules.rules.head.compoundRule.get.rules.head.simpleRule.isEmpty)
    assert(rules.rules.head.compoundRule.get.rules.head.compoundRule.get.rules.size.equals(2))
    assert(rules.rules.head.compoundRule.get.rules.head.compoundRule.get.rules.head.compoundRule.isEmpty)

    assert(rule.scoreDistribution.get.size.equals(5))
    assert(rule.scoreDistribution.get.head.value.contains("drugA"))
    assert(rule.scoreDistribution.get.head.recordCount.equals(2.0))
    assert(rule.scoreDistribution.get.head.confidence.isEmpty)
    assert(rule.scoreDistribution.get.head.probability.isEmpty)
    assert(rule.id.get.contains("RULE1"))
    assert(rule.score.contains("drugB"))
    assert(rule.recordCount.get.equals(79.0))
    assert(rule.nbCorrect.get.equals(76.0))
    assert(rule.confidence.get.equals(0.9))
    assert(rule.weight.equals(0.9))

    assert(rules.recordCount.contains(1000.0))
    assert(rules.nbCorrect.contains(149.0))
    assert(rules.defaultScore.contains("drugY"))
    assert(rules.defaultConfidence.contains(0.0))
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).contains(Some("Drug")))
    assert(Some(model.get.functionName).get.equals("classification"))
    assert(Some(model.get.algorithmName).get.contains("RuleSet"))
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
  }

  it should "provide information on the target fields if available" in {
    val targets = evaluator.getTargetFields

    assert(Some(targets.get(0).getName).get.getValue.contains("$C-Drug"))
    assert(Some(targets.get(0).getDataType).get.value().contains("string"))
    assert(Some(targets.get(0).getOpType).get.value().contains("categorical"))
  }

  it should "return results from observation inputs" in {
    assert(evaluator.verify().equals(())) // is empty

    val inputFields = evaluator.getInputFields.asScala.map {
      _.getName.getValue
    }.toSet[Any]

    val testData = Map[Any, Any]("BP" -> "HIGH", "Na" -> 0.5023, "Cholesterol" -> "HIGH", "Age" -> 36, "K" -> 0.0621d)
    val observations = testData.filterKeys(inputFields)

    val arguments = createArguments(pmmlModel, observations)
    val results = evaluator.evaluate(arguments)

    val field = FieldName.create("$C-Drug")
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("result=drugA, confidence_entries=[drugB=0.3, drugA=0.32], entityId=RULE2}"))
  }
}
