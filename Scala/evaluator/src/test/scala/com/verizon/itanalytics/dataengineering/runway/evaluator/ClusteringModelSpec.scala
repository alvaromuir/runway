package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class ClusteringModelSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("clustering")
  val testDataPath = mapData("clustering")

  "the evaluator" should
    "read Clustering models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "clustering"

    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
  }

  it should "provide information on required input fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val dataDictionary = pmmlModel.dataDictionary
    val taxonomies = Some(dataDictionary.taxonomies).get.toList.head
    val dataFields = Some(dataDictionary.dataFields).get.toList.head

    assert(dataDictionary.numOfFields.equals(10))
    assert(taxonomies.isEmpty)
    assert(dataFields.size.equals(10))

    assert(dataFields.head.name.contains("Age"))
    assert(dataFields.head.displayName.isEmpty)
    assert(dataFields.head.optype.contains("continuous"))
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.contains("0"))
    assert(dataFields.head.intervals.size.equals(1))
    assert(dataFields.head.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.head.intervals.get.head.leftMargin.contains(17.0))
    assert(dataFields.head.intervals.get.head.rightMargin.contains(90.0))

    assert(dataFields.last.name.contains("TARGET_Adjusted"))
    assert(dataFields.last.displayName.isEmpty)
    assert(dataFields.last.optype.contains("continuous"))
    assert(dataFields.last.taxonomy.isEmpty)
    assert(dataFields.last.isCyclic.get.contains("0"))
    assert(dataFields.last.intervals.size.equals(1))
    assert(dataFields.last.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.last.intervals.get.head.leftMargin.contains(0.0))
    assert(dataFields.last.intervals.get.head.rightMargin.contains(1.0))
  }

  it should "identified as a Clustering model in the PMML Schema" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.clusteringModel).isDefined)
  }

  it should "have the appropriate key-value pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.clusteringModel.get.modelName).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.functionName).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.algorithmName).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.modelClass).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.numberOfClusters).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.isScorable).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.miningSchema).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.output).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.modelStats).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.modelExplanation).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.localTransformation).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.comparisonMeasure).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.clusteringFields).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.missingValueWeights).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.clusters).isDefined)
    assert(Some(pmmlModel.clusteringModel.get.modelVerification).isDefined)
  }

  it should "have basic string-based fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(
      Some(pmmlModel.clusteringModel.get.modelName).contains(Some("k-means")))
    assert(
      Some(pmmlModel.clusteringModel.get.functionName).contains("clustering"))
    assert(Some(pmmlModel.clusteringModel.get.algorithmName).get.isEmpty)
    assert(
      Some(pmmlModel.clusteringModel.get.modelClass).get.equals("centerBased"))
    assert(Some(pmmlModel.clusteringModel.get.isScorable).get.contains(true))
  }

  it should "have basic numeric-based fields" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.clusteringModel.get.numberOfClusters).contains(4))
  }

  it should "have schema information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val miningSchema = Some(pmmlModel.clusteringModel.get.miningSchema)
    val miningFields = miningSchema.get.miningFields.get.toList

    assert(miningFields.size.equals(9))
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

  it should "provide output information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.clusteringModel.get.output).get.isEmpty)
  }

  it should "have statistics information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.clusteringModel.get.modelStats).get.isEmpty)
  }

  it should "provide information on the target fields if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)

    val targets = evaluator.getTargetFields
    assert(Some(targets.get(0).getName).contains(null))
    assert(Some(targets.get(0).getOpType).get.value().contains("categorical"))
    assert(Some(targets.get(0).getDataType).get.value().contains("string"))
    assert(Some(targets.get(0).getCategories).contains(null))
    assert(Some(targets.get(0).getMiningField).contains(null))
    assert(Some(targets.get(0).getTarget).contains(null))
  }

  it should "provide information on model explanation if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(pmmlModel.clusteringModel.get.modelExplanation.isEmpty)
  }

  it should "have local transformation information if available" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val derivedFields = Some(
      pmmlModel.clusteringModel.get.localTransformation.get.derivedFields.head)
    assert(derivedFields.get.size.equals(48))
    assert(derivedFields.get.head.name.get.contains("Private_Employment"))
    assert(Some(derivedFields.get.head.displayName).contains(null))
    assert(derivedFields.get.head.optype.contains("ordinal"))
    assert(derivedFields.get.head.dataType.contains("integer"))
  }

  it should "have information on the models verification" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.clusteringModel.get.modelVerification).get.isEmpty)
  }

  it should "provide information on compare measurements" in {
    // revisit this for other Measure types
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val compareMeasure = Some(pmmlModel.clusteringModel.get.comparisonMeasure)
    assert(compareMeasure.isDefined)

    assert(compareMeasure.get.kind.contains("distance"))
    assert(compareMeasure.get.compareFunction.contains("absDif"))
    assert(compareMeasure.get.minimum.isEmpty)
    assert(compareMeasure.get.maximum.isEmpty)
    assert(compareMeasure.get.measure.get.contains("SquaredEuclidean"))
  }

  it should "provide information on handling missing value weights" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(pmmlModel.clusteringModel.get.missingValueWeights.isEmpty)
  }

  it should "provide information on clusters" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val clusters = pmmlModel.clusteringModel.get.clusters
    assert(clusters.size.equals(pmmlModel.clusteringModel.get.numberOfClusters))

    assert(Some(clusters.head.id).get.isEmpty)
    assert(Some(clusters.head.name).get.contains("cluster_0"))
    assert(Some(clusters.head.size).get.contains(289))
    assert(Some(clusters.head.covariances).get.isEmpty)
    assert(Some(clusters.head.kohonenMap).get.isEmpty)
  }

  it should "return results from observation inputs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val inputFields = evaluator.getInputFields.asScala.map {
      _.getName.getValue
    }.toSet[Any]

    val testData = readDataFile(new File(testDataPath), lineNum = 10).head
    val observations = testData.filterKeys(inputFields)

    val arguments = createArguments(pmmlModel, observations)
    val results = evaluator.evaluate(arguments)

    assert(Some(results.asScala.keys.head).contains(null))
    assert(results.asScala(null).toString.contains("result=4,"))
    assert(results.asScala(null).toString.contains("entityId=4"))
  }

}
