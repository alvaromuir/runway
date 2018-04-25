package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/GaussianProcess.html
trait GaussianProcess
    extends MiningSchema
    with Output
    with Statistics
    with ModelExplanation
    with Target
    with TransformationDictionary
    with ModelVerification {

  case class GaussianProcess(
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
  )
}
