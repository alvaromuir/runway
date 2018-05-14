package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 25, 2018
 */

// http://dmg.org/pmml/v4-3/Taxonomy.html
trait Taxonomy extends Extension{
  case class Taxonomy(extension: Option[Seq[Extension]] = None,
                      name: String,
                      childParents: Option[Seq[ChildParent]] = None)

  case class ChildParent(extension: Option[Seq[Extension]] = None,
                         childField: String,
                         parentField: String,
                         parentLevelField: Option[String] = None,
                         isRecursive: String = "false",
                         tableLocator: Option[TableLocator] = None,
                         inlineTables: Option[InlineTable] = None)

  case class TableLocator(extension: Option[Seq[Extension]] = None)

  case class InlineTable(extension: Option[Seq[Extension]] = None,
                         row: Option[Seq[Row]])

  case class Row(content: Iterable[String])
}
