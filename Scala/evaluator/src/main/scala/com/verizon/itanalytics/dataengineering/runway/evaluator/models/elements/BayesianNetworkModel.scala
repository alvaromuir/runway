package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 25, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/BayesianNetwork.html
trait BayesianNetworkModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Targets
    with LocalTransformation
    with TestDistributions
    with ModelVerification {

  case class BayesianNetworkModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      bayesianNetworkNodes: BayesianNetworkNodes, // toDo: fix this
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      isScorable: Option[Boolean]
  )

  case class BayesianNetworkNodes(extension: Option[Seq[Extension]] = None,
                                  content: Option[Seq[Content]] = None,
                                  discreteNode: Option[DiscreteNode] = None,
                                  continuousNode: Option[ContinuousNode] = None)

  case class Content(locator: Locator)

  case class Locator(publicId: Option[String] = None,
                     systemId: Option[String] = None,
                     lineNumber: Int = 1,
                     columnNumber: Int = 1)

  case class ContinuousNode(
      extension: Option[Seq[Extension]] = None,
      name: String,
      count: Option[Double] = None,
      derivedField: Option[DerivedField] = None,
      continuousConditionalProbability: ContinuousConditionalProbability,
      continuousDistribution: ContinuousDistribution,
      valueProbability: Option[ValueProbability] = None)

  case class ContinuousConditionalProbability(
      count: Option[Double] = None,
      parentValue: Option[ParentValue] = None,
      continuousDistribution: ContinuousDistribution)

  case class ValueProbability(value: String, probability: Double)

  case class DiscreteNode(
      extension: Option[Seq[Extension]] = None,
      name: String,
      count: Option[Double] = None,
      derivedField: Option[DerivedField] = None,
      discreteConditionalProbability: Option[DiscreteProbability] = None,
      valueProbability: Option[ValueProbability] = None)

  case class DiscreteProbability(parentValue: ParentValue,
                                 valueProbability: ValueProbability)

  case class ParentValue(parent: String, value: String)

}
