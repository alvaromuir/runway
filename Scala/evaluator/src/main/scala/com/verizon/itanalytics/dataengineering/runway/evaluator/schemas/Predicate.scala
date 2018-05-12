package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 11, 2018
 */

trait Predicate {
  case class Predicate(
      simplePredicate: Option[SimplePredicate] = None,
      compoundPredicate: Option[CompoundPredicate] = None,
      simpleSetPredicate: Option[SimpleSetPredicate] = None,
      `true`: Boolean = true,
      `false`: Boolean = false
  )

  case class SimplePredicate(
      extension: Option[Seq[Extension]] = None,
      field: String,
      operator: String,
      value: Option[String] = None
  )

  case class CompoundPredicate(
      extension: Option[Seq[Extension]] = None,
      booleanOperator: String
  )

  case class SimpleSetPredicate(
      extension: Option[Seq[Extension]] = None,
      field: String
  )
}
