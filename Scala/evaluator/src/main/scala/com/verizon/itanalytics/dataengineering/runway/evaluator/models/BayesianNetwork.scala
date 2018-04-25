package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/BayesianNetwork.html
trait BayesianNetwork
    extends MiningSchema
    with Output
    with Statistics
    with ModelExplanation
    with Target
    with TransformationDictionary
    with TestDistributions
    with ModelVerification {

  case class BayesianNetwork(
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      isScorable: Option[Boolean],
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Target]] = None,
      localTransformation: Option[LocalTransformation] = None,
      bayesianNetworkNodes: String, // toDo: fix this
      modelVerification: Option[ModelVerification] = None
  )

  case class BayesianNetworkNodes(continuousNode: Option[ContinuousNode] = None,
                                  discreteNode: Option[DiscreteNode] = None)

  case class ContinuousNode(
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
      name: String,
      count: Option[Double] = None,
      derivedField: Option[DerivedField] = None,
      discreteConditionalProbability: Option[DiscreteProbability] = None,
      valueProbability: Option[ValueProbability] = None)

  case class DiscreteProbability(parentValue: ParentValue,
                                 valueProbability: ValueProbability)

  case class ParentValue(parent: String, value: String)

}
