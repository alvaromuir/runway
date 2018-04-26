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
    with TrainingInstances
    with ModelVerification {

  case class GaussianProcess(
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      optimizer: Option[String] = None,
      isScorable: Option[Boolean],
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Target]] = None,
      localTransformation: Option[LocalTransformation] = None,
      radialBasisKernel: Option[RadialBasisKernel] = None,
      aRDSquaredExponentialKernel: Option[ARDSquaredExponentialKernel] = None,
      absoluteExponentialKernel: Option[AbsoluteExponentialKernel] = None,
      generalizedExponentialKernel: Option[GeneralizedExponentialKernel] = None,
      trainingInstances: TrainingInstances
  )

  case class RadialBasisKernel(description: Option[String] = None,
                               gamma: Double = 1,
                               noiseVariance: Double = 1,
                               lambda: Option[Lambda] = None)

  case class ARDSquaredExponentialKernel(description: Option[String] = None,
                                         gamma: Double = 1,
                                         noiseVariance: Double = 1,
                                         lambda: Option[Lambda] = None)

  case class AbsoluteExponentialKernel(description: Option[String] = None,
                                       gamma: Double = 1,
                                       noiseVariance: Double = 1,
                                       lambda: Option[Lambda] = None)

  case class GeneralizedExponentialKernel(description: Option[String] = None,
                                          gamma: Double = 1,
                                          noiseVariance: Double = 1,
                                          dagree: Double = 1,
                                          lambda: Option[Lambda] = None)

  case class Lambda(n: Int, `type`: String, lambda: Option[Seq[Double]] = None)
}
