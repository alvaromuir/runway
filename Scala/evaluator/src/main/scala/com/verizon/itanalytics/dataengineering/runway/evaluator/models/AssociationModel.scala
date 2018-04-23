package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._
import com.verizon.itanalytics.dataengineering.runway.schemas._

// http://dmg.org/pmml/v4-3/AssociationRules.html
trait AssociationModel
    extends Statistics
    with TransformationDictionary
    with MiningSchema
    with ModelVerification
    with Output {

  case class AssociationModel(
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
      isScorable: Option[Boolean],
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      localTransformation: Option[LocalTransformation] = None,
      miningSchema: MiningSchema,
      items: Option[Seq[Item]] = None,
      itemSets: Option[Seq[ItemSet]] = None,
      associationRules: Option[Seq[AssociationRule]] = None,
      modelVerification: Option[ModelVerification] = None)

  case class Item(id: String,
                  value: String,
                  field: Option[String] = None,
                  category: Option[String] = None,
                  mappedValue: Option[String] = None,
                  weight: Option[Double] = None)

  case class ItemSet(id: String,
                     support: Option[Double] = None,
                     numberOfItems: Option[Int] = None,
                     itemRefs: Seq[ItemRef])

  case class ItemRef(itemRef: String)

  case class AssociationRule(antecedent: String,
                             consequent: String,
                             support: Double,
                             confidence: Double,
                             lift: Option[Double] = None,
                             leverage: Option[Double] = None,
                             affinity: Option[Double] = None,
                             id: Option[String] = None)

}
