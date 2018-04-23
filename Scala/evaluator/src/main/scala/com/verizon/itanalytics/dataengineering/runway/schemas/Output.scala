package com.verizon.itanalytics.dataengineering.runway.schemas

// http://dmg.org/pmml/v4-3/Output.html
trait Output {

  case class Output(outputFields: Seq[OutputField])

  case class OutputField(name: String,
                         displayName: Option[String] = None,
                         optype: String,
                         targetField: Option[String] = None,
                         feature: String = "predictedValue",
                         value: Option[String] = None,
                         ruleFeature: String = "consequent",
                         algorithm: String = "exclusiveRecommendation",
                         rank: Int,
                         rankBasis: String = "confidence",
                         rankOrder: String = "descending",
                         isMultiValued: String = "0",
                         segmentId: Option[String] = None,
                         isFinalResult: Boolean = true)
}