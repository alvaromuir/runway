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
    implicit val rowFormat: RootJsonFormat[Row] = jsonFormat1(Row)
    implicit val inlineTableFormat: RootJsonFormat[InlineTable] = jsonFormat2(InlineTable)
    implicit val tableLocatorFormat: RootJsonFormat[TableLocator] = jsonFormat1(TableLocator)
    implicit val childParentFormat: RootJsonFormat[ChildParent] = jsonFormat7(ChildParent)
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


