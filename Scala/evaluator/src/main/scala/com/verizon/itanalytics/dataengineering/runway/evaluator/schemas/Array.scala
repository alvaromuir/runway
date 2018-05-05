package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 02, 2018
*/


trait Array {
  case class Array(n: Int, `type`: String, value: String)

}
