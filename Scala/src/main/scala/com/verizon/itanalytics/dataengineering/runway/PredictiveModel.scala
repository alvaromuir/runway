package com.verizon.itanalytics.dataengineering.runway

import java.util

import org.dmg.pmml.{FieldName, PMML}
import org.dmg.pmml.mining.MiningModel
import org.jpmml.evaluator.{FieldValue, ModelEvaluator, _}
import org.jpmml.model.PMMLUtil
import _root_.java.io.{File, FileInputStream, FileNotFoundException, FileOutputStream}

import javax.xml.bind.JAXBException
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.mutable

object PredictiveModel {

  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  @throws[JAXBException]
  @throws[FileNotFoundException]
  def readPMML(file: File): PMML = try {
    val input = new FileInputStream(file)
    try { PMMLUtil.unmarshal(input) }
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


  def getArguments(line: String,
                       inputFields: util.List[InputField],
                       modelEvaluator: ModelEvaluator[MiningModel]): mutable.LinkedHashMap[FieldName, FieldValue] = {

    val lineVariables = line.split(",")
    val arguments = new mutable.LinkedHashMap[FieldName, FieldValue]

    if (lineVariables.size - 1 != inputFields.size) return arguments

    // todo: Write dynamic transform logic
    for ((_, idx) <- lineVariables.zipWithIndex.take(lineVariables.zipWithIndex.length - 1)) {
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


