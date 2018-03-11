package com.verizon.itanalytics.dataengineering.runway

import java.io.File

import scala.util.control.Breaks.{break, breakable}
import scala.collection.JavaConversions._

import com.verizon.itanalytics.dataengineering.runway.PredictiveModel._

object Runway extends App {

  // Model
  val pmmlFile = "./src/test/resources/test.pmml"
  val pMML = readPMML(new File(pmmlFile))
  val algorithmName = pMML.getModels.head.getAlgorithmName


  // Observations
  val dataFile = "./src/test/resources/test.csv"
  val data = readDataSet(new File(dataFile))
  val line = data.getLines.slice(1, 2).next

  // initialize evaluator
  val evaluator = getEvaluator(pMML)
  evaluator.verify()

  // Score dataset
  val inputFields = evaluator.getInputFields
  val arguments = getArguments(line, inputFields, evaluator)
  val results = evaluator.evaluate(arguments)
  val outputFields = evaluator.getOutputFields

  // return results
  for(line <- data.getLines) {
    breakable {
      if(line.startsWith("sepal_length")) break
      else if (line.isEmpty) break
      else if (line.split(",").size != inputFields.size) break
      else {
        val arguments = getArguments(line, inputFields, evaluator)
        val results = evaluator.evaluate(arguments)
        val outputFields = evaluator.getOutputFields
        for (outputField <- outputFields) {
          val outputFieldName = outputField.getName
          if (outputFieldName.toString.startsWith("Predicted")) {
            println(s"algorithm: $algorithmName")
            println(inputFields.map(x => x.getName).mkString(", "))
            println(s"data set: ${line.split(",").mkString(", ")}")
            println(s"result: ${outputFieldName.toString} - ${results.get(outputFieldName)}")
            println
          }
        }
      }
    }
  }
}