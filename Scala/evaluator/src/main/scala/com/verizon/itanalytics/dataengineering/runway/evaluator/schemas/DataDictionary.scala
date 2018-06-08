package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

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
}

