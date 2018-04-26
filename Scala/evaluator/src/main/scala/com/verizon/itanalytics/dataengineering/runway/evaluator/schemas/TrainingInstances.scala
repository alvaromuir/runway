package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

trait TrainingInstances extends Taxonomy {
  case class TrainingInstances(
      isTransformed: Boolean = false,
      recordCount: Option[Int],
      fieldCount: Option[Int],
      instanceFields: Option[Iterable[InstanceField]] = None,
      tableLocator: Option[String],
      inlineTables: Option[Seq[Row]] = None
  )

  case class InstanceField(field: String,
                           column: Option[String] = None,
                           key: Option[String] = None)

}
