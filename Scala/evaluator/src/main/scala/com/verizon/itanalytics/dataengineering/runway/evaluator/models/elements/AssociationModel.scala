package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

// http://dmg.org/pmml/v4-3/AssociationRules.html
trait AssociationModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with LocalTransformation
    with TransformationDictionary
    with ModelVerification {

  case class AssociationModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      localTransformations: Option[LocalTransformation] = None,
      item: Option[Seq[Item]] = None,
      itemSet: Option[Seq[ItemSet]] = None,
      associationRule: Option[Seq[AssociationRule]] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      numberOfTransactions: Int,
      maxNumberOfItemsPerTA: Option[Int] = None,
      avgNumberOfItemsPerTA: Option[Double] = None,
      minimumSupport: Double,
      minimumConfidence: Double,
      lengthLimit: Option[Int] = None,
      numberOfItems: Int,
      numberOfItemsets: Int,
      numberOfRules: Int,
      isScorable: Option[Boolean] = Option(true)
  )

  case class Item(extension: Option[Seq[Extension]] = None,
                  id: String,
                  value: String,
                  field: Option[String] = None,
                  category: Option[String] = None,
                  mappedValue: Option[String] = None,
                  weight: Option[Double] = None)

  case class ItemSet(extension: Option[Seq[Extension]] = None,
                     id: String,
                     support: Option[Double] = None,
                     numberOfItems: Option[Int] = None,
                     itemRefs: Seq[ItemRef])

  case class ItemRef(extension: Option[Seq[Extension]] = None, itemRef: String)

  case class AssociationRule(extension: Option[Seq[Extension]] = None,
                             antecedent: String,
                             consequent: String,
                             support: Double,
                             confidence: Double,
                             lift: Option[Double] = None,
                             leverage: Option[Double] = None,
                             affinity: Option[Double] = None,
                             id: Option[String] = None)
}
