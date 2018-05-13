package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 06, 2018
 */

// http://dmg.org/pmml/v4-3/NeuralNetwork.html
trait NeuralNetworkModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with CompareMeasure
    with ModelVerification {

  case class NeuralNetworkModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      neuralInputs: NeuralInputs,
      neuralLayer: Seq[NeuralLayer],
      neuralOutputs: NeuralOutputs,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      activationFunction: String,
      normalizationMethod: Option[String] = None,
      threshold: Double = 1.0,
      numberOfHiddenLayers: Int,
      isScorable: Option[Boolean]
  )

  case class NeuralInputs(
      extension: Option[Seq[Extension]] = None,
      numberOfInputs: Int,
      neuralInput: Iterable[NeuralInput]
  )
  case class NeuralInput(
      extension: Option[Seq[Extension]] = None,
      derivedField: DerivedField,
      id: String
  )

  case class NeuralLayer(extension: Option[Seq[Extension]] = None,
                         neurons: Seq[Neuron],
                         activationFunction: Option[String] = None,
                         threshold: Option[Double] = None,
                         width: Option[Double] = None,
                         altitude: Option[Double] = None,
                         normalizationMethod: Option[String] = None)

  case class Neuron(
      extension: Option[Seq[Extension]] = None,
      id: String,
      bias: Option[Double] = None,
      width: Option[Double] = None,
      altitude: Option[Double] = None
  )

  case class NeuralOutputs(
      extension: Option[Seq[Extension]] = None,
      numberOfOutputs: Int,
      neuralOutput: Iterable[NeuralOutput]
  )

  case class NeuralOutput(extension: Option[Seq[Extension]] = None,
                          derivedField: DerivedField,
                          outputNeuron: String)

}
