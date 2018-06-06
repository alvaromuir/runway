package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/

// http://dmg.org/pmml/v4-3/MiningSchema.html#xsdElement_MiningSchema
trait MiningSchema {
  case class MiningSchema(miningFields: Option[Seq[MiningField]] = None)

  case class MiningField(name: String,
                         usageType: String = "active",
                         optype: Option[String] = None,
                         importance: Option[Double],
                         outliers: String = "asIs",
                         lowValue: Option[Double],
                         highValue: Option[Double],
                         missingValueReplacement: Option[String] = None,
                         missingValueTreatment: Option[String] = None,
                         invalidValueTreatment: String = "returnInvalid")

  // todo: these need to be used as pick lists
  case class OutlierTreatmentMethod()
  case class MissingValueTreatmentMethod()
  case class InvalidValueTreatmentMethod()

}
