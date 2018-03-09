package com.verizon.itanalytics.dataengineering.runway

import java.io.File
import java.util

import com.verizon.itanalytics.dataengineering.runway.PredictiveModel.{getArguments, readPMML}
import org.dmg.pmml.PMML
import org.jpmml.evaluator.InputField
import org.jpmml.evaluator.mining.MiningModelEvaluator

import scala.io.Source
import scala.util.control.Breaks.{break, breakable}
import scala.collection.JavaConversions._

// This is temporary, just a test run
object Runway extends App {

  val sourceDir = "./src/test/resources"

  val pmmlFileName = "test.pmml"
  val pmmlFile = sourceDir + "/" + pmmlFileName
  val pMML: PMML = readPMML(new File(pmmlFile))
  val evaluator = new MiningModelEvaluator(pMML)

  val dataFileName = "iris.csv"
  val dataFile = sourceDir + "/" + dataFileName
  val data = Source.fromFile(dataFile)

  val inputFields:util.List[InputField] = evaluator.getInputFields

  var count = 0
  for(line <- data.getLines) {
    breakable {
      if(line.startsWith("sepal_length")) break
      else if(line.isEmpty) break
      else {
        val arguments = getArguments(line, inputFields, evaluator)
        val results = evaluator.evaluate(arguments)
        val targetFields = evaluator.getTargetFields
        val outputFields = evaluator.getOutputFields
        for (outputField <- outputFields) {
          if (outputField.getName.toString == "Predicted_Species") {
            count += 1
            println(s"observation ${count} - Predicted_Species : ${results.get(outputField.getName)}")
          }
        }
      }
    }
  }
}
