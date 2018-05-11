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
 * 04 27, 2018
 */

class GeneralRegressionSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("generalRegression")
  val testDataPath = mapData("generalRegression")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[GeneralRegression] = pmmlModel.generalRegressionModel

  "the evaluator" should
    "read General Regression models" in {
    val modelClass = "General regression"

    assert(evaluator.getSummary == modelClass)

  }

  it should "provide information on required input fields" in {
    val dataDictionary = pmmlModel.dataDictionary
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
    assert(dataFields.head.intervals.get.isEmpty)
    assert(dataFields.head.intervals.get.isEmpty)
    assert(dataFields.head.intervals.get.isEmpty)
  }

  it should "identify as an General Regression model in the PMML Schema" in {
    assert(model.isDefined)
  }

  it should "contain the appropriate key-value pairs" in {
    assert(Some(model.get.extension).isDefined)
    assert(Some(model.get.miningSchema).isDefined)
    assert(Some(model.get.output).isDefined)
    assert(Some(model.get.modelStats).isDefined)
    assert(Some(model.get.modelExplanation).isDefined)
    assert(Some(model.get.localTransformations).isDefined)
    assert(Some(model.get.parameterList).isDefined)
    assert(Some(model.get.factorsList).isDefined)
    assert(Some(model.get.covariateList).isDefined)
    assert(Some(model.get.pPMatrix).isDefined)
    assert(Some(model.get.pCovMatrix).isDefined)
    assert(Some(model.get.paramMatrix).isDefined)
    assert(Some(model.get.eventValues).isDefined)
    assert(Some(model.get.baseCumHazardTables).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.targetVariableName).isDefined)
    assert(Some(model.get.modelType).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)

    assert(Some(model.get.targetReferenceCategory).isDefined)
    assert(Some(model.get.cumulativeLink).isDefined)
    assert(Some(model.get.linkFunction).isDefined)
    assert(Some(model.get.linkParameter).isDefined)
    assert(Some(model.get.trialsVariable).isDefined)
    assert(Some(model.get.trialsValue).isDefined)
    assert(Some(model.get.distribution).isDefined)
    assert(Some(model.get.distParameter).isDefined)
    assert(Some(model.get.offsetVariable).isDefined)
    assert(Some(model.get.offsetValue).isDefined)
    assert(Some(model.get.modelDF).isDefined)
    assert(Some(model.get.endTimeVariable).isDefined)
    assert(Some(model.get.startTimeVariable).isDefined)
    assert(Some(model.get.subjectIDVariable).isDefined)
    assert(Some(model.get.statusVariable).isDefined)
    assert(Some(model.get.baselineStrataVariable).isDefined)
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
    assert(miningFields.head.usageType.contains("predicted"))
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

  it should "provide local transformation information, if available" in {
    val localTransformations = model.get.localTransformations

    assert(localTransformations.isEmpty)
  }

  it should "provide General Regression Model parameter list information" in {
    val parameterList = model.get.parameterList.head

    assert(parameterList.size.equals(6))
    assert(parameterList.head.key.contains("p0"))
    assert(parameterList.head.label.get.contains("Intercept"))
    assert(parameterList.head.name.contains("p0"))
    assert(parameterList.head.referencePoint.equals(0.0))
  }

  it should "provide factor list if information available" in {
    val factorsList = model.get.factorsList.get

    assert(factorsList.head.name.contains("class"))
    assert(factorsList.head.categories.isEmpty)
    assert(factorsList.head.contrastMatrixType.isEmpty)
    assert(factorsList.head.matrix.isEmpty)


  }

  it should "provide General Regression Model covariate list information " in {
    val covariateList = model.get.covariateList.get

    assert(Some(covariateList.toList.head.name).contains("sepal_width"))
    assert(Some(covariateList.toList.head.categories).get.isEmpty)
    assert(Some(covariateList.toList.head.contrastMatrixType).get.isEmpty)
    assert(Some(covariateList.toList.head.matrix).get.isEmpty)

  }

  it should "provide General Regression Model pPMatrix information" in {
    val pPMatrix = model.get.pPMatrix.get

    assert(Some(pPMatrix.toList.head.parameterName).get.contains("p1"))
    assert(Some(pPMatrix.toList.head.predictorName).get.contains("sepal_width"))
    assert(Some(pPMatrix.toList.head.targetCategory).get.isEmpty)
    assert(Some(pPMatrix.toList.head.value.get).get.contains("1"))
  }

  it should "provide pCovMatrix information if available" in {
    val pCovMatrix = model.get.pCovMatrix

    assert(Some(pCovMatrix).get.isEmpty)
  }

  it should "provide General Regression Model paramMatrix information" in {
    val paramMatrix = model.get.paramMatrix.get

    assert(Some(paramMatrix.head.parameterName).contains("p0"))
    assert(Some(paramMatrix.head.targetCategory).get.isEmpty)
    assert(Some(paramMatrix.head.beta).contains(2.22413677241697))
    assert(Some(paramMatrix.head.df).contains(1))
  }

  it should "provide General Regression Model event values information" in {
    val eventValues = model.get.eventValues

    assert(Some(eventValues).get.isEmpty)
  }

  it should "base cumulative hazard tables" in {
    val baseCumHazardTables = model.get.baseCumHazardTables

    assert(Some(baseCumHazardTables).get.isEmpty)
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.targetVariableName).get.isEmpty)
    assert(Some(model.get.modelType).get.equals("generalizedLinear"))
    assert(Some(model.get.modelName).contains(Some("General_Regression_Model")))
    assert(Some(model.get.functionName).get.equals("regression"))
    assert(Some(model.get.algorithmName).get.contains("glm"))
    assert(Some(model.get.targetReferenceCategory).get.isEmpty)
    assert(Some(model.get.cumulativeLink).get.isEmpty)
    assert(Some(model.get.linkFunction).get.contains("identity"))
    assert(Some(model.get.trialsVariable).get.isEmpty)
    assert(Some(model.get.distribution).get.contains("normal"))
    assert(Some(model.get.offsetVariable).get.isEmpty)
    assert(Some(model.get.startTimeVariable).get.isEmpty)
    assert(Some(model.get.endTimeVariable).get.isEmpty)
    assert(Some(model.get.subjectIDVariable).get.isEmpty)
    assert(Some(model.get.statusVariable).get.isEmpty)
    assert(Some(model.get.baselineStrataVariable).get.isEmpty)
  }

  it should "provide General Regression Model basic numeric-based fields" in {
    assert(Some(model.get.linkParameter).get.isEmpty)
    assert(Some(model.get.trialsValue).get.isEmpty)
    assert(Some(model.get.distParameter).get.isEmpty)
    assert(Some(model.get.offsetValue).get.isEmpty)
    assert(Some(model.get.modelDF).get.isEmpty)
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
  }

  it should "provide information on the target fields if available" in {
    val targets = evaluator.getTargetFields

    assert(Some(targets.get(0).getName.getValue).get.contains("sepal_length"))
    assert(Some(targets.get(0).getOpType).get.value().contains("continuous"))
    assert(Some(targets.get(0).getDataType).get.value().contains("double"))
    assert(Some(targets.get(0).getCategories).contains(null))
    assert(Some(targets.get(0).getMiningField).head.getName.getValue.equals("sepal_length"))
    assert(Some(targets.get(0).getMiningField.getOpType).contains(null))
    assert(Some(targets.get(0).getMiningField).head.getUsageType.value().equals("predicted"))
    assert(Some(targets.get(0).getMiningField.getImportance).contains(null))
    assert(Some(targets.get(0).getMiningField.getLowValue).contains(null))
    assert(Some(targets.get(0).getMiningField.getHighValue).contains(null))
    assert(Some(targets.get(0).getMiningField.getOutlierTreatment).get.value().contains("asIs"))
    assert(Some(targets.get(0).getMiningField.getMissingValueTreatment).contains(null))
    assert(Some(targets.get(0).getMiningField.getMissingValueReplacement).contains(null))
    assert(Some(targets.get(0).getMiningField.getInvalidValueTreatment).get.value().contains("returnInvalid"))
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

    val field = FieldName.create("sepal_length")
    val response = """(?<=\{result=)(.*?)(?=\})""".r

    assert(Some(results.asScala.keys.head).contains(field))
    assert(response.findFirstIn(results.asScala(field).toString).contains("4.988997258952446"))
  }
}
