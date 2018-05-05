package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 26, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/GaussianProcess.html
trait GaussianProcessModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with TrainingInstances
    with ModelVerification {

  case class GaussianProcess(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      radialBasisKernel: Option[RadialBasisKernel] = None,
      aRDSquaredExponentialKernel: Option[ARDSquaredExponentialKernel] = None,
      absoluteExponentialKernel: Option[AbsoluteExponentialKernel] = None,
      generalizedExponentialKernel: Option[GeneralizedExponentialKernel] = None,
      trainingInstances: TrainingInstances,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      optimizer: Option[String] = None,
      isScorable: Option[Boolean]
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
