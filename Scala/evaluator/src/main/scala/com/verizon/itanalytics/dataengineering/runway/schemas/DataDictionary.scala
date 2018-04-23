package com.verizon.itanalytics.dataengineering.runway.schemas

// http://dmg.org/pmml/v4-3/DataDictionary.html
trait DataDictionary extends Taxonomy {

  case class DataDictionary(numOfFields: Int,
                            taxonomies: Option[Seq[Taxonomy]] = None,
                            dataFields: Option[Seq[DataField]])

  case class DataField(name: String,
                       displayName: Option[String] = None,
                       optype: String,
                       dataType: String,
                       taxonomy: Option[String] = None,
                       isCyclic: Option[String] = None,
                       intervals: Option[Seq[Interval]] = None)

  case class Interval(closure: String,
                      leftMargin: Option[Double] = None,
                      rightMargin: Option[Double] = None)
}
