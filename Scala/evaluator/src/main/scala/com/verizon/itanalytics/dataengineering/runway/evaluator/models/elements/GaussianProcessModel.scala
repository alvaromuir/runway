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

  case class GaussianProcessModel(
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
      trainingInstances: Option[TrainingInstances] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      optimizer: Option[String] = None,
      isScorable: Option[Boolean]
  )

  case class RadialBasisKernel(extension: Option[Seq[Extension]] = None,
                               description: Option[String] = None,
                               gamma: Double = 1,
                               noiseVariance: Double = 1,
                               lambda: Double = 1)

  case class ARDSquaredExponentialKernel(extension: Option[Seq[Extension]] = None,
                                         lambda: Option[Seq[Lambda]] = None,
                                         description: Option[String] = None,
                                         gamma: Double = 1,
                                         noiseVariance: Double = 1)

  case class AbsoluteExponentialKernel(extension: Option[Seq[Extension]] = None,
                                       lambda: Option[Seq[Lambda]] = None,
                                       description: Option[String] = None,
                                       gamma: Double = 1,
                                       noiseVariance: Double = 1)

  case class GeneralizedExponentialKernel(extension: Option[Seq[Extension]] = None,
                                          lambda: Option[Seq[Lambda]] = None,
                                          description: Option[String] = None,
                                          gamma: Double = 1,
                                          noiseVariance: Double = 1,
                                          degree: Double = 1)

  case class Lambda(n: Int, `type`: String, value: String)
}
