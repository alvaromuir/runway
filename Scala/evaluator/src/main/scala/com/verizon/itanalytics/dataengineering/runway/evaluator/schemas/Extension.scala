package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait Extension {

  case class Extension(
      extender: Option[String] = None,
      name: Option[String] = None,
      value: Option[String] = None,
      content: Option[Seq[String]] = None
  )

  implicit object ExtensionFormat extends JsonFormat[Extension] {
    def write(extension: Extension) = JsObject(
      extension.extender match { case _ => "extender" -> JsString(extension.extender.get) },
      extension.name match { case _ => "name" -> JsString(extension.name.get) },
      extension.value match { case _ => "value" -> JsString(extension.value.get) },
      extension.content match { case _ => "content" -> JsArray(extension.content.get.map(JsString(_)).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

}