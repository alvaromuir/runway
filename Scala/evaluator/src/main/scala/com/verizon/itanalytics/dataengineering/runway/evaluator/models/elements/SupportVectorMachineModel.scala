package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 05, 2018
 */

// http://dmg.org/pmml/v4-3/SupportVectorMachine.html
trait SupportVectorMachineModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with ModelVerification
    with Content
    with Array {

  case class SupportVectorMachineModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      kernel: String, // probably should be a kernel type
      vectorDictionary: VectorDictionary,
      supportVectorMachine: Seq[SupportVectorMachine],
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      threshold: Option[Double] = Option(0),
      svmRepresentation: Option[String], // SupportVectors or Coefficients
      classificationMethod: Option[String] = None, // OneAgainstAll or OneAgainstOne
      alternateBinaryTargetCategory: Option[String] = None,
      maxWins: Option[Boolean],
      isScorable: Option[Boolean] = Option(true)
  )

  case class LinearKernelType(extension: Option[Seq[Extension]] = None,
                              description: Option[String] = None)

  case class PolynomialKernelType(extension: Option[Seq[Extension]] = None,
                                  description: Option[String] = None,
                                  gamma: Option[Double] = Option(1),
                                  coef0: Option[Double] = Option(1),
                                  degree: Option[Double] = Option(1))

  case class RadialBiasKernelType(extension: Option[Seq[Extension]] = None,
                                  description: Option[String] = None,
                                  gamma: Option[Double] = Option(1))

  case class SigmoidKernelType(extension: Option[Seq[Extension]] = None,
                               description: Option[String] = None,
                               gamma: Option[Double] = Option(1),
                               coef0: Option[Double] = Option(1))

  case class VectorDictionary(extension: Option[Seq[Extension]] = None,
                              vectorFields: VectorFields,
                              vectorInstance: Option[Iterable[VectorInstance]] =
                                None,
                              numberOfVectors: Option[Int] = None)

  case class VectorFields(extension: Option[Seq[Extension]] = None,
                          numberOfFields: Option[Int] = None,
                            content: Option[Seq[Content]] = None )

  case class VectorInstance(extension: Option[Seq[Extension]] = None,
                            id: String,
                            key: Option[String] = None,
                            array: Option[Array] = None,
                            realSparseArray: Option[RealSparseArray] = None)

  case class SupportVectorMachine(
      extension: Option[Seq[Extension]] = None,
      supportVectors: Option[SupportVectors] = None,
      coefficients: Coefficients,
      targetCategory: Option[String] = None,
      alternateTargetCategory: Option[String] = None,
      threshold: Option[Double])

  case class SupportVectors(extension: Option[Seq[Extension]] = None,
                            supportVector: Iterable[SupportVector],
                            numberOfSupportVectors: Option[Int] = None,
                            numberOfAttributes: Option[Int] = None)

  case class SupportVector(extension: Option[Seq[Extension]] = None,
                           vectorId: String)

  case class Coefficients(extension: Option[Seq[Extension]] = None,
                          coefficient: Iterable[Coefficient],
                          numberOfCoefficients: Option[Int] = None,
                          absoluteValue: Option[Double] = Option(0))

  case class Coefficient(extension: Option[Seq[Extension]] = None,
                         value: Option[Double] = Option(0))

}
