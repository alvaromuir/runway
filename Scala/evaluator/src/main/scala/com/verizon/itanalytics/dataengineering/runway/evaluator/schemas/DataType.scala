package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import java.time.{LocalDate, LocalDateTime, LocalTime}

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 12, 2018
 */

trait DataType {
  case class DataType(
      string: Option[String] = None,
      integer: Option[Int] = None,
      float: Option[Float] = None,
      double: Option[Double] = None,
      boolean: Option[Boolean] = None,
      date: Option[LocalDate] = None,
      time: Option[LocalTime] = None,
      dateTime: Option[LocalDateTime] = None,
      `dateDaysSince[0]`: Option[LocalDate] = None,
      `dateDaysSince[1960]`: Option[LocalDate] = None,
      `dateDaysSince[1970]`: Option[LocalDate] = None,
      `dateDaysSince[1980]`: Option[LocalDate] = None,
      timeSeconds: Option[LocalDate] = None,
      `dateTimeSecondsSince[0]`: Option[LocalDate] = None,
      `dateTimeSecondsSince[1960]`: Option[LocalDate] = None,
      `dateTimeSecondsSince[1970]`: Option[LocalDate] = None,
      `dateTimeSecondsSince[1980]`: Option[LocalDate] = None
  )
}
