package com.verizon.itanalytics.dataengineering.runway.evaluator.models

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 29, 2018
*/

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/

// http://dmg.org/pmml/v4-3/AssociationRules.html
trait AssociationRules
    extends Statistics
    with TransformationDictionary
    with MiningSchema
    with ModelVerification
    with Output {

  case class AssociationRules(
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
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      localTransformation: Option[LocalTransformation] = None,
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
