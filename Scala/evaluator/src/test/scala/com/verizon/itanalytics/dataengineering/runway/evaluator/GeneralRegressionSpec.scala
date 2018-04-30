package com.verizon.itanalytics.dataengineering.runway.evaluator

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 27, 2018
 */

import java.io.File

import scala.collection.JavaConverters._

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

class GeneralRegressionSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("generalRegression")
  val testDataPath = mapData("generalRegression")

  "the evaluator" should
    "read Gaussian Process models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "classification"

    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
  }

  it should "provide information on required input fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val dataDictionary = pmmlModel.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(6))
    assert(taxonomies.isEmpty)

    assert(dataFields.size.equals(6))
    assert(dataFields.head.name.contains("sepal length"))
    assert(dataFields.head.displayName.isEmpty)
    assert(dataFields.head.optype.contains("continuous"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.contains("0"))
    assert(dataFields.head.intervals.get.isEmpty)

    assert(dataFields.tail.head.name.contains("sepal width"))
    assert(dataFields.tail.head.displayName.isEmpty)
    assert(dataFields.tail.head.optype.contains("continuous"))
    assert(dataFields.tail.head.taxonomy.isEmpty)
    assert(dataFields.tail.head.isCyclic.get.contains("0"))
    assert(dataFields.tail.head.intervals.get.isEmpty)
  }

  it should "identified as an Association Rules model in the PMML Schema" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.generalRegressionModel).isDefined)
  }

  it should "have the appropriate key-value pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.generalRegressionModel.get.modelName).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.modelType).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.targetVariableName).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.functionName).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.algorithmName).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.targetReferenceCategory).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.cumulativeLink).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.linkFunction).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.linkParameter).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.trialsVariable).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.trialsValue).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.distribution).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.distParameter).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.offsetVariable).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.offsetValue).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.modelDF).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.startTimeVariable).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.endTimeVariable).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.subjectIDVariable).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.statusVariable).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.baselineStrataVariable).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.isScorable).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.miningSchema).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.output).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.modelStats).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.modelExplanation).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.targets).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.localTransformation).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.parameterList).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.factorsList).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.covariateList).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.pPMatrix).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.pCovMatrix).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.paramMatrix).isDefined)
    assert(Some(pmmlModel.generalRegressionModel.get.eventValues).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.baseCumHazardTables).isDefined)
    assert(
      Some(pmmlModel.generalRegressionModel.get.modelVerification).isDefined)
  }

  it should "have basic string-based fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(
      Some(pmmlModel.generalRegressionModel.get.modelName)
        .contains(Some("IRIS_NOMREG")))
    assert(
      Some(pmmlModel.generalRegressionModel.get.modelType).get
        .equals("multinomialLogistic"))
    assert(
      Some(pmmlModel.generalRegressionModel.get.targetVariableName).get
        .equals("$L-SPECIES"))
    assert(
      Some(pmmlModel.generalRegressionModel.get.functionName).get
        .equals("classification"))
    assert(
      Some(pmmlModel.generalRegressionModel.get.algorithmName).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.targetReferenceCategory).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.cumulativeLink).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.linkFunction).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.trialsVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.distribution).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.offsetVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.startTimeVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.endTimeVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.subjectIDVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.statusVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.baselineStrataVariable).get.isEmpty
    )
    assert(
      Some(pmmlModel.generalRegressionModel.get.isScorable).get.contains(true))
  }

  it should "have basic numeric-based fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.generalRegressionModel.get.linkParameter).get.isEmpty)
    assert(Some(pmmlModel.generalRegressionModel.get.trialsValue).get.isEmpty)
    assert(Some(pmmlModel.generalRegressionModel.get.distParameter).get.isEmpty)
    assert(Some(pmmlModel.generalRegressionModel.get.offsetValue).get.isEmpty)
    assert(Some(pmmlModel.generalRegressionModel.get.modelDF).get.isEmpty)
  }

  it should "have schema information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val miningSchema = Some(pmmlModel.generalRegressionModel.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(6))
    assert(miningFields.head.name.contains("sepal length"))
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

  it should "provide output information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.generalRegressionModel.get.output).get.isEmpty)
  }

  it should "have statistics information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.generalRegressionModel.get.modelStats).get.isEmpty)
  }

  it should "provide information on model explanation if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(pmmlModel.generalRegressionModel.get.modelExplanation.isEmpty)
  }

  it should "provide information on the target fields if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)

    val targets = evaluator.getTargetFields
    assert(Some(targets.get(0).getName.getValue).get.equals("$L-SPECIES"))
    assert(Some(targets.get(0).getOpType).get.value().contains("categorical"))
    assert(Some(targets.get(0).getDataType).get.value().contains("string"))
    assert(Some(targets.get(0).getCategories).head.asScala.toList.equals(List[String]("Iris-setosa", "Iris-versicolor", "Iris-virginica")))
    assert(Some(targets.get(0).getMiningField).head.getName.getValue.equals("$L-SPECIES"))
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

  it should "have local transformation information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val localTransformation = pmmlModel.generalRegressionModel.get.localTransformation

    assert(localTransformation.isEmpty)
  }

  it should "have parameter list information" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val parameterList = pmmlModel.generalRegressionModel.get.parameterList.head

    assert(parameterList.size.equals(5))
    assert(parameterList.head.key.contains("P0000001"))
    assert(parameterList.head.label.contains("<<Intercept>>"))
    assert(parameterList.head.name.contains("P0000001"))
    assert(parameterList.head.referencePoint.equals(0.0))
  }

  it should "provide factor list if information available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val factorsList = pmmlModel.generalRegressionModel.get.factorsList.get

    assert(factorsList.isEmpty)

  }

  it should "have covariate list information " in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val covariateList = pmmlModel.generalRegressionModel.get.covariateList.get

    assert(Some(covariateList.toList.head.name).contains("sepal length"))
    assert(Some(covariateList.toList.head.categories).get.isEmpty)
    assert(Some(covariateList.toList.head.contrastMatrixType).get.isEmpty)
    assert(Some(covariateList.toList.head.matrix).get.isEmpty)

  }

  it should "have pPMatrix information" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val pPMatrix = pmmlModel.generalRegressionModel.get.pPMatrix.get

    assert(Some(pPMatrix.toList.head.parameterName).get.contains("P0000002"))
    assert(Some(pPMatrix.toList.head.predictorName).get.contains("sepal length"))
    assert(Some(pPMatrix.toList.head.targetCategory).get.isEmpty)
    assert(Some(pPMatrix.toList.head.value.get).get.contains("1"))
  }

  it should "provide pCovMatrix information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val pCovMatrix = pmmlModel.generalRegressionModel.get.pCovMatrix

    assert(Some(pCovMatrix).get.isEmpty)
  }

  it should "have paramMatrix information" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val paramMatrix = pmmlModel.generalRegressionModel.get.paramMatrix.get

    assert(Some(paramMatrix.head.parameterName).contains("P0000001"))
    assert(Some(paramMatrix.head.targetCategory).get.contains("Iris-setosa"))
    assert(Some(paramMatrix.head.beta).contains(33.1502815864775))
    assert(Some(paramMatrix.head.df).contains(1))
  }

  it should "have event values information" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val eventValues = pmmlModel.generalRegressionModel.get.eventValues

    assert(Some(eventValues).get.isEmpty)
  }

  it should "base cumulative hazard tables" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val baseCumHazardTables = pmmlModel.generalRegressionModel.get.baseCumHazardTables

    assert(Some(baseCumHazardTables).get.isEmpty)
  }

  it should "have information on the models verification" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.generalRegressionModel.get.modelVerification).get.isEmpty)
  }
}
