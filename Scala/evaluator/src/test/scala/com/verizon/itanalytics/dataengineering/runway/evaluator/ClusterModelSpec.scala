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
* 04 26, 2018
*/

class ClusterModelSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  val testModelPath = mapModels("clustering")
  val testDataPath = mapData("clustering")
  val pMML: PMML = readPMML(new File(testModelPath))
  val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[ClusteringModel] = pmmlModel.clusteringModel

  "the evaluator" should
    "read Clustering models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "clustering"

    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
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
    assert(dataFields.head.taxonomy.isEmpty)
    assert(dataFields.head.isCyclic.get.contains("0"))
    assert(dataFields.head.intervals.size.equals(1))
    assert(dataFields.head.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.head.intervals.get.head.leftMargin.contains(17.0))
    assert(dataFields.head.intervals.get.head.rightMargin.contains(90.0))
  }

  it should "identify as a Clustering model in the PMML Schema" in {
    assert(Some(model).isDefined)
  }

  it should "contain the appropriate key-value pairs" in {
    assert(Some(model.get.extension).isDefined)
    assert(Some(model.get.miningSchema).isDefined)
    assert(Some(model.get.output).isDefined)
    assert(Some(model.get.modelStats).isDefined)
    assert(Some(model.get.modelExplanation).isDefined)
    assert(Some(model.get.localTransformations).isDefined)
    assert(Some(model.get.comparisonMeasure).isDefined)
    assert(Some(model.get.clusteringFields).isDefined)
    assert(Some(model.get.missingValueWeights).isDefined)
    assert(Some(model.get.cluster).isDefined)
    assert(Some(model.get.modelVerification).isDefined)

    assert(Some(model.get.modelName).isDefined)
    assert(Some(model.get.functionName).isDefined)
    assert(Some(model.get.algorithmName).isDefined)
    assert(Some(model.get.modelClass).isDefined)
    assert(Some(model.get.numberOfClusters).isDefined)
    assert(Some(model.get.isScorable).isDefined)
  }

  it should "return extension information, if available" in {
    assert(Some(model.get.extension).get.get.isEmpty)
  }

  it should "provide mining schema information, if available" in {
    val miningSchema = Some(model.get.miningSchema)
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

    assert(derivedFields.get.size.equals(48))
    assert(derivedFields.get.head.name.get.contains("Private_Employment"))
    assert(derivedFields.get.head.displayName.isEmpty)
    assert(derivedFields.get.head.optype.contains("ordinal"))
    assert(derivedFields.get.head.dataType.contains("integer"))
  }

  it should "provide Clustering Model information on compare measurements" in {
    // revisit this for other Measure types
    val compareMeasure = Some(model.get.comparisonMeasure)
    assert(compareMeasure.isDefined)

    assert(compareMeasure.get.kind.contains("distance"))
    assert(compareMeasure.get.compareFunction.contains("absDif"))
    assert(compareMeasure.get.minimum.isEmpty)
    assert(compareMeasure.get.maximum.isEmpty)
    assert(compareMeasure.get.measure.get.contains("SquaredEuclidean"))
  }

  it should "provide Clustering Model information on clustering fields" in {
    val clusteringFields = Some(model.get.clusteringFields)
    assert(clusteringFields.isDefined)

    assert(clusteringFields.get.size.equals(52))
    assert(clusteringFields.get.head.extension.get.isEmpty)
    assert(clusteringFields.get.head.field.equals("Age"))
    assert(clusteringFields.get.head.isCenterField.contains("true"))
    assert(clusteringFields.get.head.fieldWeight.equals(1.0))
    assert(clusteringFields.get.head.compareFunction.contains("absDiff"))

  }

  it should "provide Clustering Model information on handling missing value weights" in {
    assert(model.get.missingValueWeights.isEmpty)
  }

  it should "provide Clustering Model information on clusters" in {
    val clusters = model.get.cluster
    assert(clusters.size.equals(model.get.numberOfClusters))

    assert(Some(clusters.head.id).get.isEmpty)
    assert(Some(clusters.head.name).get.contains("cluster_0"))
    assert(Some(clusters.head.size).get.contains(289))
    assert(Some(clusters.head.covariances).get.isEmpty)
    assert(Some(clusters.head.kohonenMap).get.isEmpty)
  }

  it should "provide model verification information, if available" in {
    assert(Some(model.get.modelVerification).get.isEmpty)
  }

  it should "return basic string-based fields" in {
    assert(Some(model.get.modelName).contains(Some("k-means")))
    assert(Some(model.get.functionName).contains("clustering"))
    assert(Some(model.get.algorithmName).get.isEmpty)
    assert(Some(model.get.modelClass).get.equals("centerBased"))
  }

  it should "return model-specific numeric-based fields" in {
    assert(Some(model.get.numberOfClusters).contains(4))
  }

  it should "return basic boolean fields" in {
    assert(Some(model.get.isScorable).get.contains(true))
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

  it should "return results from observation inputs" in {
    assert(evaluator.verify().equals(())) // is empty

    val inputFields = evaluator.getInputFields.asScala.map {
      _.getName.getValue
    }.toSet[Any]

    val testData = readDataFile(new File(testDataPath), lineNum = 10).head
    val observations = testData.filterKeys(inputFields)

    val arguments = createArguments(pmmlModel, observations)
    val results = evaluator.evaluate(arguments)

    val field = null
    assert(Some(results.asScala.keys.head).contains(field))
    assert(Some(results.asScala(field)).get.toString.contains("result=4,"))
    assert(Some(results.asScala(field)).get.toString.contains("entityId=4"))
  }

}
