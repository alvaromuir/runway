package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Calendar, Locale}

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.scalatest.FlatSpec

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/

class PMMLSchemaSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {
  val testModelPath = mapModels("association")

  "the evaluator" should
    "read Association models" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val miningFunction = "associationRules"

    assert(pMML.getModels.get(0).getMiningFunction.value() == miningFunction)
  }

  it should "create evaluator instances from files" in {
    val unevaluatedPmml: PMML = readPMML(new File(testModelPath))
    val evaluatedPmml = evaluatePmml(unevaluatedPmml)

    val miningFunction = "associationRules"
    assert(
      evaluatedPmml.getPMML.getModels
        .get(0)
        .getMiningFunction
        .value() == miningFunction)
  }

  it should "parse association pMML files to a PmmlModel case class" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel).isDefined)
    assert(Some(pmmlModel.header).isDefined)
    assert(Some(pmmlModel.miningBuildTask).isDefined)
    assert(Some(pmmlModel.dataDictionary).isDefined)
    assert(Some(pmmlModel.transformationDictionary).isDefined)
    assert(Some(pmmlModel.version).isDefined)
  }

  it should "have a header with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
    val timeStamp = Calendar.getInstance().getTime

    assert(Some(pmmlModel.header.copyright).isDefined)
    assert(Some(pmmlModel.header.description).isDefined)
    assert(Some(pmmlModel.header.modelVersion).isDefined)
    assert(Some(pmmlModel.header.application).isDefined)
    assert(Some(pmmlModel.header.annotations).isDefined)
    assert(Some(pmmlModel.header.timeStamp).isDefined)

    assert(
      Some(pmmlModel.header.copyright).contains(Some("Copyright (c) 2012 DMG")))
    assert(
      Some(pmmlModel.header.description)
        .contains(Some("arules association rules model")))
    assert(Some(pmmlModel.header.modelVersion).get.isEmpty)
    assert(Some(pmmlModel.header.application.get.name).contains("Rattle/PMML"))
    assert(
      Some(pmmlModel.header.application.get.version).contains(Some("1.2.30")))
    assert(Some(pmmlModel.header.annotations).get.get.isEmpty)
    assert(
      Some(pmmlModel.header.timeStamp.get.dropRight(3))
        .contains(dateFormat.format(timeStamp)))
  }

  it should "have mining build tasks with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.miningBuildTask).get.isEmpty)
  }

  it should "have a data dictionary tasks with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.dataDictionary).get.dataFields.isDefined)
    assert(Some(pmmlModel.dataDictionary).get.taxonomies.isDefined)
    assert(Some(pmmlModel.dataDictionary).get.taxonomies.get.isEmpty)
  }

  it should "have a transformation dictionary with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.transformationDictionary).get.isEmpty)
  }

  it should "have an pMML appropriate version number" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlModel: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlModel.version).contains("4.3"))
  }

}
