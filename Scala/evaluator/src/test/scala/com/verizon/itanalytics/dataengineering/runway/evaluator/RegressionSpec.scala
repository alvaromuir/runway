package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File

import scala.collection.JavaConverters._
import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
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
  val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)
  val model: Option[GeneralRegression] = pmmlModel.generalRegressionModel

  "the evaluator" should
    "read Regression models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "classification"

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
    assert(dataFields.head.isCyclic.get.equals("0"))
    assert(dataFields.head.intervals.size.equals(1))
    assert(dataFields.head.intervals.get.head.closure.equals("closedClosed"))
    assert(dataFields.head.intervals.get.head.leftMargin.contains(17.0))
    assert(dataFields.head.intervals.get.head.rightMargin.contains(90.0))
  }

  it should "identify as a Clustering model in the PMML Schema" in {
    assert(Some(model).isDefined)
  }


}
