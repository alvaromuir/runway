package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._
/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 02, 2018
 */

trait Array {
  case class Array(n: Int, `type`: String, value: String)

  case class RealArray(n: Double, `type`: String, value: String)

  case class IntArray(n: Int, `type`: String, value: String)

  case class IntSparseArray(indices: Seq[Int],
                            entries: Seq[Int],
                            n: Int,
                            defaultValue: Option[Int] = Option(0))

  case class RealSparseArray(indices: Seq[Double],
                             entries: Seq[Double],
                             n: Int,
                             defaultValue: Option[Double] = Option(0))


  implicit object ArrayProtocol extends DefaultJsonProtocol {
    implicit val arrayFormat: RootJsonFormat[Array] =  jsonFormat3(Array)
    implicit val realArrayFormat: RootJsonFormat[Array] =  jsonFormat3(Array)
    implicit val intArrayFormat: RootJsonFormat[Array] =  jsonFormat3(Array)
  }

  implicit object IntSparseArrayFormat extends JsonFormat[IntSparseArray] {
    def write(intSparseArray: IntSparseArray) = JsObject(
      "indices" -> JsArray(intSparseArray.indices.map(JsNumber(_)).toVector),
      "entries" -> JsArray(intSparseArray.entries.map(JsNumber(_)).toVector),
      "n" -> JsNumber(intSparseArray.n),
      intSparseArray.defaultValue match { case _ => "defaultValue" -> JsNumber(intSparseArray.defaultValue.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }


  implicit object RealSparseArrayFormat extends JsonFormat[RealSparseArray] {
    def write(realSparseArray: RealSparseArray) = JsObject(
      "indices" -> JsArray(realSparseArray.indices.map(JsNumber(_)).toVector),
      "entries" -> JsArray(realSparseArray.entries.map(JsNumber(_)).toVector),
      "n" -> JsNumber(realSparseArray.n),
      realSparseArray.defaultValue match { case _ => "defaultValue" -> JsNumber(realSparseArray.defaultValue.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }







}