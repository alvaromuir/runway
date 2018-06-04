package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._

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

  implicit object RowFormat extends JsonFormat[Row] {
    def write(row: Row) = JsObject(
      "content" -> JsArray(row.content.map(JsString(_)).toVector)
    )
    def read(json: JsValue) = null // not implemented
  }

  implicit object InlineTableFormat extends JsonFormat[InlineTable] {
    def write(inlineTable: InlineTable) = JsObject(
      inlineTable.extension match { case _ => "extension" -> inlineTable.extension.toJson },
      inlineTable.row match { case _ => "row" -> inlineTable.row.toJson }
    )
    def read(json: JsValue) = null // not implemented
  }

  implicit object TableLocator extends JsonFormat[TableLocator] {
    def write(tableLocator: TableLocator) = JsObject(
      tableLocator.extension match { case _ => "extension" -> tableLocator.extension.toJson }
    )
    def read(json: JsValue) = null // not implemented
  }

  implicit object ChildParent extends JsonFormat[ChildParent] {
    def write(childParent: ChildParent) = JsObject(
      childParent.extension match { case _ => "extension" -> childParent.extension.toJson },
      "childField" -> JsString(childParent.childField),
      "parentField" -> JsString(childParent.parentField),
      childParent.parentLevelField match { case _ => "parentLevelField" -> JsString(childParent.parentLevelField.get) },
      "isRecursive" -> JsBoolean(childParent.isRecursive.toBoolean),
      "tableLocator" -> childParent.tableLocator.toJson,
      "inlineTables" -> childParent.inlineTables.toJson
    )
    def read(json: JsValue) = null // not implemented
  }
}


