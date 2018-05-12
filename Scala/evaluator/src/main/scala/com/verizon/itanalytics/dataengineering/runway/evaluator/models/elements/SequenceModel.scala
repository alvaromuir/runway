package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/Sequence.html
trait SequenceModel
    extends Extension
    with MiningSchema
    with ModelStats
    with LocalTransformation
    with AssociationModel
    with Content {

  case class SequenceModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      modelStats: Option[ModelStats] = None,
      localTransformations: Option[LocalTransformation] = None,
      constraints: Option[Constraints] = None,
      item: Option[Seq[Item]] = None,
      itemSet: Option[Seq[ItemSet]] = None,
      setPredicate: Option[Seq[SetPredicate]] = None,
      sequence: Seq[Sequence],
      sequenceRule: Option[SequenceRule] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      numberOfTransactions: Option[Int] = None,
      maxNumberOfItemsPerTransaction: Option[Int] = None,
      avgNumberOfItemsPerTransaction: Option[Double] = None,
      numberOfTransactionGroups: Option[Int] = None,
      maxNumberOfTAsPerTAGroup: Option[Int] = None,
      avgNumberOfTAsPerTAGroup: Option[Double] = None,
      isScorable: Option[Boolean]
  )

  case class Constraints(extension: Option[Seq[Extension]] = None,
                         minimumNumberOfItems: Int = 1,
                         maximumNumberOfItems: Option[Int] = None,
                         minimumNumberOfAntecedentItems: Int = 1,
                         maximumNumberOfAntecedentItems: Option[Int] = None,
                         minimumNumberOfConsequentItems: Int = 1,
                         maximumNumberOfConsequentItems: Option[Int] = None,
                         minimumSupport: Double = 0,
                         minimumConfidence: Double = 0,
                         minimumLift: Double = 0,
                         minimumTotalSequenceTime: Double = 0,
                         maximumTotalSequenceTime: Option[Double] = None,
                         minimumItemsetSeparationTime: Double = 0,
                         maximumItemsetSeparationTime: Option[Double] = None,
                         minimumAntConsSeparationTime: Double = 0,
                         maximumAntConsSeparationTime: Option[Double] = None)

  // deprecated as of spec 3.1
  case class SetPredicate(extension: Option[Seq[Extension]] = None,
                          id: String,
                          field: String,
                          operator: Option[String] = None)

  case class Sequence(extension: Option[Seq[Extension]] = None,
                      setReference: SetReference,
                      id: String,
                      key: Option[String] = None,
                      time: Option[Time] = None,
                      content: Option[Seq[Content]] = None,
                      numberOfSets: Option[Int] = None,
                      occurrence: Option[Int] = None,
                      support: Option[Double] = None)

  case class Time(extension: Option[Seq[Extension]] = None,
                  min: Option[Double] = None,
                  max: Option[Double] = None,
                  mean: Option[Double] = None,
                  standardDeviation: Option[Double] = None)

  case class SetReference(extension: Option[Seq[Extension]] = None,
                          setId: String)

  case class SequenceRule(extension: Option[Seq[Extension]] = None,
                          antecedentSequence: AntecedentSequence,
                          delimiter: Delimiter,
                          consequentSequence: ConsequentSequence)

  case class AntecedentSequence()

  case class Delimiter()

  case class ConsequentSequence()
}
