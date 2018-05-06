package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 26, 2018
 */

trait TrainingInstances extends Taxonomy {
  case class TrainingInstances(
      extension: Option[Seq[Extension]] = None,
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
