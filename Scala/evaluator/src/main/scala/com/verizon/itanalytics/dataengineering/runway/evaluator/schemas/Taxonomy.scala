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

  implicit object TaxonomyProtocol extends DefaultJsonProtocol {
    implicit val childParentFormat: RootJsonFormat[ChildParent] = jsonFormat7(ChildParent)
  }


  implicit object RowFormat extends JsonFormat[Row] {
    def write(row: Row) = JsObject(
      "content" -> JsArray(row.content.map(JsString(_)).toVector)
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object InlineTableFormat extends JsonFormat[InlineTable] {
    def write(inlineTable: InlineTable) = JsObject(
      inlineTable.extension match { case _ => "extension" -> JsArray(inlineTable.extension.get.map(_.toJson).toVector) },
      inlineTable.row match { case _ => "row" -> JsArray(inlineTable.row.get.map(_.toJson).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object TableLocatorFormat extends JsonFormat[TableLocator] {
    def write(tableLocator: TableLocator) = JsObject(
      tableLocator.extension match { case _ => "extension" -> JsArray(tableLocator.extension.get.map(_.toJson).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object ChildParentFormat extends JsonFormat[ChildParent] {
    def write(childParent: ChildParent) = JsObject(
      childParent.extension match { case _ => "extension" -> JsArray(childParent.extension.get.map(_.toJson).toVector) },
      "childField" -> JsString(childParent.childField),
      "parentField" -> JsString(childParent.parentField),
      childParent.parentLevelField match { case _ => "parentLevelField"  -> JsString(childParent.parentLevelField.get) },
      "isRecursive" -> JsBoolean(childParent.isRecursive.toBoolean),
      childParent.tableLocator match { case _ => "tableLocator" -> childParent.tableLocator.get.toJson },
      childParent.inlineTables match { case _ => "InlineTables" -> childParent.inlineTables.get.toJson }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object TaxonomyFormat extends JsonFormat[Taxonomy] {
    def write(taxonomy: Taxonomy) = JsObject(
      taxonomy.extension match { case _ => "extension" -> JsArray(taxonomy.extension.get.map(_.toJson).toVector) },
      "name" -> JsString(taxonomy.name),
      taxonomy.childParents match { case _ => "childParents" -> JsArray(taxonomy.childParents.get.map(_.toJson).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }
}


