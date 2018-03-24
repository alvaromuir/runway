package com.verizon.itanalytics.dataengineering.runway.evaluator

import java.util

import org.dmg.pmml.{FieldName, PMML}
import org.jpmml.evaluator.{FieldValue, _}
import org.jpmml.model.PMMLUtil
import _root_.java.io.{File, FileInputStream, FileNotFoundException, FileOutputStream}

import javax.xml.bind.JAXBException
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.{BufferedSource, Source}

object Manager {

  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  @throws[JAXBException]
  @throws[FileNotFoundException]
  def readPMML(file: File): PMML = try {
    val input = new FileInputStream(file)
    try { PMMLUtil.unmarshal(input) }
    finally if (input != null) input.close()
  }

  @throws[JAXBException]
  @throws[FileNotFoundException]
  def readDataSet(file: File): BufferedSource = try {
    val input = new FileInputStream(file)
    try { Source.fromFile(file) }
    finally if (input != null) input.close()
  }

  def getEvaluator(pMML: PMML): Evaluator = {
    val valueFactoryFactory: ValueFactoryFactory = ReportingValueFactoryFactory.newInstance
    val modelEvaluatorFactory: ModelEvaluatorFactory = ModelEvaluatorFactory.newInstance

    modelEvaluatorFactory.setValueFactoryFactory(valueFactoryFactory)
    modelEvaluatorFactory.newModelEvaluator(pMML)
  }

  @throws[Exception]
  def writePMML(pMML: PMML, file: File): Unit = {
    try {
      val output = new FileOutputStream(file)
      try
        PMMLUtil.marshal(pMML, output)
      finally if (output != null) output.close()
    }
  }




  // todo: Write better arguments class. This one is demo-specific
  def getArguments(line: String,
                       inputFields: util.List[InputField],
                       modelEvaluator: Evaluator): mutable.LinkedHashMap[FieldName, FieldValue] = {

    val lineVariables = line.split(",")
    val arguments = new mutable.LinkedHashMap[FieldName, FieldValue]

    if (lineVariables.size != inputFields.size) return arguments

    // todo: Write dynamic transform logic
    for ((_, idx) <- lineVariables.zipWithIndex.take(lineVariables.zipWithIndex.length)) {
      arguments(new FieldName(inputFields(idx).getName.toString)) =
        inputFields(idx).prepare(
          lineVariables(idx) match {
            case x if x.isEmpty  => 0
            case _ => lineVariables(idx)
          })
    }
    arguments
  }

}