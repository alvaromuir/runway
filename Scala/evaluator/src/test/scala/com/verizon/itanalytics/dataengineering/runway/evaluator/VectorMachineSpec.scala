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
 * 05 13, 2018
 */

class VectorMachineSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("supportVectorMachine")
  val testDataPath = mapData("supportVectorMachine")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[SupportVectorMachineModel] = pmmlSchema.supportVectorMachineModel

  "the evaluator" should
    "read Support Vector Machine models" in {
    val modelClass = "Support vector machine"

    assert(evaluator.getSummary == modelClass)
  }

  it should "provide information on required input fields" in {
    val dataDictionary = pmmlSchema.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(10))
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

  it should "identify as a Support Vector Machine model in the PMML Schema" in {
    assert(Some(model).isDefined)
  }

  it should "contain the appropriate key-value pairs" in {
    assert(Some(model.get.extension).isDefined)
    assert(Some(model.get.miningSchema).isDefined)
    assert(Some(model.get.output).isDefined)
    assert(Some(model.get.modelStats).isDefined)
    assert(Some(model.get.modelExplanation).isDefined)
    assert(Some(model.get.localTransformations).isDefined)
    assert(Some(model.get.kernel).isDefined)
    assert(Some(model.get.vectorDictionary).isDefined)
    assert(Some(model.get.supportVectorMachine).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)
    assert(Some(model.get.threshold).isDefined)
    assert(Some(model.get.svmRepresentation).isDefined)
    assert(Some(model.get.classificationMethod).isDefined)
    assert(Some(model.get.alternateBinaryTargetCategory).isDefined)
    assert(Some(model.get.maxWins).isDefined)
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
    val derivedFields = Some(
      model.get.localTransformations.get.derivedFields.head)

    assert(derivedFields.get.size.equals(54))
    assert(derivedFields.get.head.name.get.contains("Private_Employment"))
    assert(derivedFields.get.head.displayName.isEmpty)
    assert(derivedFields.get.head.optype.contains("ordinal"))
    assert(derivedFields.get.head.dataType.contains("integer"))
  }

  it should "provide Support Vector Machine model information on the kernel" in {
    val kernel = Some(model.get.kernel)

    assert(kernel.get.contains("PolynomialKernel"))
  }

  it should "provide Support Vector Machine model information the vector dictionary" in {
    val dictionary = Some(model.get.vectorDictionary)
    val vectorInstance = dictionary.get.vectorInstance.get.head
    val realSparseArray = vectorInstance.realSparseArray.get

    assert(dictionary.get.vectorFields.numberOfFields.get.equals(53))
    assert(dictionary.get.vectorFields.content.get.head.locator.isEmpty)
    assert(vectorInstance.id.contains("1_1038288"))
    assert(vectorInstance.key.get.contains("1_1038288"))
    assert(vectorInstance.array.isEmpty)
    assert(realSparseArray.indices.size.equals(53))
    assert(realSparseArray.indices.head.equals(1.0))
    assert(realSparseArray.entries.size.equals(53))
    assert(realSparseArray.entries.head.equals(0.4694971021222236))
    assert(realSparseArray.n.equals(53))
    assert(realSparseArray.defaultValue.get.equals(0.0))
    assert(dictionary.get.numberOfVectors.get.equals(741))
  }

  it should "provide Support Vector Machine model information the support vector machine" in {
    val svm = Some(model.get.supportVectorMachine)
    val supportVectors = svm.get.head.supportVectors.get
    val supportVector = svm.get.head.supportVectors.get.supportVector.head
    val coefficients = svm.get.head.coefficients
    val coefficient = coefficients.coefficient.head

    assert(supportVector.vectorId.contains("1_1038288"))
    assert(supportVectors.numberOfSupportVectors.get.equals(741))
    assert(supportVectors.numberOfAttributes.get.equals(53))
    assert(coefficients.coefficient.size.equals(741))
    assert(coefficients.numberOfCoefficients.get.equals(741))
    assert(coefficients.absoluteValue.get.equals(-1.9484983196017862))
    assert(coefficient.value.get.equals(1.0))
    assert(svm.get.head.targetCategory.get.contains("0"))
    assert(svm.get.head.alternateTargetCategory.get.contains("1"))
    assert(svm.get.head.threshold.isEmpty)

  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).contains(Some("SVM")))
    assert(Some(model.get.functionName).contains("classification"))
    assert(Some(model.get.algorithmName).get.contains("Sequential Minimal Optimization (SMO)"))
  }

  it should "return model-specific numeric-based fields" in {
    assert(Some(model.get.threshold.get).get.equals(0.0))
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

    assert(Some(targets.get(0).getTarget.getField).get.getValue.contains("TARGET_Adjusted"))
    assert(Some(targets.get(0).getTarget.getOpType).get.value().contains("categorical"))
    assert(Some(targets.get(0).getTarget.getCastInteger).contains(null))
    assert(Some(targets.get(0).getTarget.getMin).contains(null))
    assert(Some(targets.get(0).getTarget.getMax).contains(null))
    assert(Some(targets.get(0).getTarget.getRescaleConstant).get.equals(0.0))
    assert(Some(targets.get(0).getTarget.getRescaleFactor).get.equals(1.0))
    assert(Some(targets.get(0).getTarget.getTargetValues).get.size.equals(2))
    assert(Some(targets.get(0).getTarget.getTargetValues.asScala.head.getValue).get.contains("0"))
    assert(Some(targets.get(0).getTarget.getTargetValues.asScala.head.getDisplayValue).contains(null))
    assert(Some(targets.get(0).getTarget.getTargetValues.asScala.head.getPriorProbability).contains(null))
    assert(Some(targets.get(0).getTarget.getTargetValues.asScala.head.getDefaultValue).contains(null))
    assert(Some(targets.get(0).getTarget.getTargetValues.asScala.head.getPartition).contains(null))
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

    val field = FieldName.create("TARGET_Adjusted")
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("result=0, vote_entries=[0=1.0]"))
  }

}
