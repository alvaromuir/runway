package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/BaselineModel.html
trait BaselineModels
    extends MiningSchema
    with Output
    with Statistics
    with ModelExplanation
    with Target
    with TransformationDictionary
    with TestDistributions
    with ModelVerification {

  case class BaselineModels(
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      mathContext: Option[String] = None,
      isScorable: Option[Boolean],
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Target]] = None,
      localTransformation: Option[LocalTransformation] = None,
      testDistributions: TestDistributions,
      modelVerification: Option[ModelVerification] = None
  )

}
