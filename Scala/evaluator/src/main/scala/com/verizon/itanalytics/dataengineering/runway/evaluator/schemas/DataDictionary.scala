package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import spray.json._

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 24, 2018
*/

// http://dmg.org/pmml/v4-3/DataDictionary.html
trait DataDictionary extends Taxonomy {

  case class DataDictionary(extension: Option[Seq[Extension]] = None,
                            numOfFields: Int,
                            taxonomies: Option[Seq[Taxonomy]] = None,
                            dataFields: Option[Seq[DataField]])

  case class DataField(extension: Option[Seq[Extension]] = None,
                       name: String,
                       displayName: Option[String] = None,
                       optype: String,
                       dataType: String,
                       taxonomy: Option[String] = None,
                       isCyclic: Option[String] = None,
                       intervals: Option[Seq[Interval]] = None)

  case class Interval(extension: Option[Seq[Extension]] = None,
                      closure: String,
                      leftMargin: Option[Double] = None,
                      rightMargin: Option[Double] = None)

  implicit object IntervalFormat extends JsonFormat[Interval] {
    def write(interval: Interval) = JsObject(
      interval.extension match { case _ => "extension" -> JsArray(interval.extension.get.map(_.toJson).toVector) },
      "closure" -> JsString(interval.closure),
      interval.leftMargin match { case _ => "leftMargin" -> JsNumber(interval.leftMargin.get) },
      interval.rightMargin match { case _ => "rightMargin" -> JsNumber(interval.rightMargin.get) }
    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object DataFieldFormat extends JsonFormat[DataField] {
    def write(dataField: DataField) = JsObject(
      dataField.extension match { case _ => "extension" -> JsArray(dataField.extension.get.map(_.toJson).toVector) },
      "name" -> JsString(dataField.name),
      dataField.displayName match { case _ => "displayName" -> JsString(dataField.displayName.get) },
      "optype" -> JsString(dataField.optype),
      "dataType" -> JsString(dataField.dataType),
      dataField.taxonomy match { case _ => "taxonomy" -> JsString(dataField.taxonomy.get) },
      dataField.isCyclic match { case _ => "isCyclic" -> JsBoolean(dataField.isCyclic.get.toBoolean) },
      dataField.intervals match { case _ => "intervals" -> JsArray(dataField.intervals.get.map(_.toJson).toVector)}

    )
    def read(json: JsValue): Null = null // not implemented
  }

  implicit object DataDictionaryFormat extends JsonFormat[DataDictionary] {
    def write(dataDictionary: DataDictionary) = JsObject(
      dataDictionary.extension match { case _ => "extension" -> JsArray(dataDictionary.extension.get.map(_.toJson).toVector) },
      "numOfField" -> JsNumber(dataDictionary.numOfFields),
      dataDictionary.taxonomies match { case _ => "taxonomies" -> JsArray(dataDictionary.taxonomies.get.map(_.toJson).toVector) },
      dataDictionary.dataFields match  { case _ => "dataFields" -> JsArray(dataDictionary.dataFields.get.map(_.toJson).toVector) }
    )
    def read(json: JsValue): Null = null // not implemented
  }
}

