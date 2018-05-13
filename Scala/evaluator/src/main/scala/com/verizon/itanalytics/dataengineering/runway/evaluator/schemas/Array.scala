package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 02, 2018
 */

trait Array {
  case class Array(n: Int, `type`: String, value: String)

  case class IntSparseArray(indices: Seq[Int],
                            entries: Seq[Int],
                            n: Int,
                            defaultValue: Option[Int] = Option(0))

  case class RealSparseArray(indices: Seq[Double],
                             entries: Seq[Double],
                             n: Int,
                             defaultValue: Option[Double] = Option(0))

}
