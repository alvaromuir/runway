package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Calendar, Locale}

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas.PMMLSchema
import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.dmg.pmml.{Model, PMML}
import org.jpmml.evaluator.ModelEvaluator
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

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
    with Evaluator
    with PMMLSchema {
  val testModelPath = mapModels("association")

  implicit val formats = Serialization.formats(NoTypeHints)

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
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlSchema).isDefined)
    assert(Some(pmmlSchema.header).isDefined)
    assert(Some(pmmlSchema.miningBuildTask).isDefined)
    assert(Some(pmmlSchema.dataDictionary).isDefined)
    assert(Some(pmmlSchema.transformationDictionary).isDefined)
    assert(Some(pmmlSchema.version).isDefined)
  }

  it should "have a header with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
    val timeStamp = Calendar.getInstance().getTime

    assert(Some(pmmlSchema.header.copyright).isDefined)
    assert(Some(pmmlSchema.header.description).isDefined)
    assert(Some(pmmlSchema.header.modelVersion).isDefined)
    assert(Some(pmmlSchema.header.application).isDefined)
    assert(Some(pmmlSchema.header.annotations).isDefined)
    assert(Some(pmmlSchema.header.timeStamp).isDefined)

    assert(
      Some(pmmlSchema.header.copyright)
        .contains(Some("Copyright (c) 2012 DMG")))
    assert(
      Some(pmmlSchema.header.description)
        .contains(Some("arules association rules model")))
    assert(Some(pmmlSchema.header.modelVersion).get.isEmpty)
    assert(Some(pmmlSchema.header.application.get.name).contains("Rattle/PMML"))
    assert(
      Some(pmmlSchema.header.application.get.version).contains(Some("1.2.30")))
    assert(Some(pmmlSchema.header.annotations).get.get.isEmpty)
    assert(
      Some(pmmlSchema.header.timeStamp.get.dropRight(3))
        .contains(dateFormat.format(timeStamp)))
  }

  it should "have mining build tasks with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlSchema.miningBuildTask).get.isEmpty)
  }

  it should "have a data dictionary tasks with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlSchema.dataDictionary).get.dataFields.isDefined)
    assert(Some(pmmlSchema.dataDictionary).get.taxonomies.isDefined)
    assert(Some(pmmlSchema.dataDictionary).get.taxonomies.get.isEmpty)
  }

  it should "have a transformation dictionary with appropriate value-pairs" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlSchema.transformationDictionary).get.isEmpty)
  }

  it should "have an pMML appropriate version number" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(Some(pmmlSchema.version).contains("4.3"))
  }

  it should "return pMML in json format" in {
    val pMML: PMML = readPMML(new File(testModelPath))
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val pmmlSchema: PMMLSchema = parsePmml(evaluator.getPMML)

    assert(write(pmmlSchema.header.application) === """{"name":"Rattle/PMML","version":"1.2.30"}""")
  }

}
