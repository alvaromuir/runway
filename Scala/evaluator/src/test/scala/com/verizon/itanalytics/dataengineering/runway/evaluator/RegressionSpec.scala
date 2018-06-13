package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import scala.collection.JavaConverters._
import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{FieldName, Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 06, 2018
*/

class RegressionSpec extends FlatSpec
  with Builder
  with TestUtils
  with Evaluator  {

  val testModelPath = mapModels("regression")
  val testDataPath = mapData("regression")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[RegressionModel] = pmmlSchema.regressionModel

  "the evaluator" should
    "read Regression models" in {
    val modelClass = "Regression"

    assert(evaluator.getSummary == modelClass)
  }

  it should "provide information on required input fields" in {
    val dataDictionary = pmmlSchema.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(7))
    assert(taxonomies.isEmpty)
    assert(dataFields.size.equals(7))

    assert(dataFields.head.name.contains("latitude"))
    assert(dataFields.head.displayName.isEmpty)
    assert(dataFields.head.optype.contains("continuous"))
    assert(dataFields.head.dataType.contains("double"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.equals("0"))
    assert(dataFields.head.intervals.size.equals(1))
    assert(dataFields.head.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.head.intervals.get.head.leftMargin.contains(-8.28))
    assert(dataFields.head.intervals.get.head.rightMargin.contains(8.97))
  }

  it should "identify as a Regression model in the PMML Schema" in {
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
    assert(Some(model.get.regressionTable).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)
    assert(Some(model.get.modelType).isDefined)

    assert(Some(model.get.targetFieldName).isDefined)
    assert(Some(model.get.normalizationMethod).isDefined)
    assert(Some(model.get.isScorable).isDefined)
  }

  it should "return extension information, if available" in {
    assert(Some(model.get.extension).get.get.isEmpty)
  }

  it should "provide mining schema information, if available" in {
    val miningSchema = Some(model.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(7))
    assert(miningFields.head.name.contains("latitude"))
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
    assert(model.get.localTransformations.isEmpty)
  }

  it should "provide Regression model information on the regression table" in {
    val regressionTables = model.get.regressionTable
    val table = regressionTables.head

    assert(regressionTables.size.equals(1))
    assert(table.categoricalPredictor.get.isEmpty)
    assert(table.predictorTerm.get.isEmpty)
    assert(table.intercept.equals(6.008706171265235))
    assert(table.targetCategory.isEmpty)
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).contains(Some("KNIME Linear Regression")))
    assert(Some(model.get.functionName).get.equals("regression"))
    assert(Some(model.get.algorithmName).get.contains("LinearRegression"))
    assert(Some(model.get.modelType).get.isEmpty)
    assert(Some(model.get.targetFieldName).get.contains("airtemp"))
    assert(Some(model.get.normalizationMethod).get.contains("none"))
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
  }

  it should "provide information on the target fields if available" in {
    val targets = evaluator.getTargetFields

    assert(Some(targets.get(0).getName).get.getValue.contains("airtemp"))
    assert(Some(targets.get(0).getDataType).get.value().contains("double"))
    assert(Some(targets.get(0).getOpType).get.value().contains("continuous"))
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

    val field = FieldName.create("airtemp")
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("Regression{result=27.464206274359626}"))
  }
}
