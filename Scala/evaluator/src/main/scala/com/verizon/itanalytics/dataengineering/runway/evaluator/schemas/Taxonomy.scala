package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

// http://dmg.org/pmml/v4-3/Taxonomy.html
trait Taxonomy {
  case class Taxonomy(name: String,
                      childParents: Option[Seq[ChildParent]] = None)

  case class ChildParent(
      childField: String,
      parentField: String,
      parentLevelField: Option[String] = None,
      isRecursive: String = "false",
      tableLocator: Option[String],
      inlineTables: Option[Seq[Row]] = None)

  case class TableLocator()

  case class Row(row: String)
}
