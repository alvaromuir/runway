package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

import java.time._
import java.time.format.DateTimeFormatter

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 12, 2018
 */

trait DataType {
  val datePattern = "MM/dd/yyyy HH:mm:ss"
  private val timeStamp = LocalDateTime.now()
  private val yearZeroTimeStamp = LocalDateTime.parse("01/01/0001 00:00:00", DateTimeFormatter.ofPattern(datePattern))
  private val ts1960 = LocalDateTime.parse("01/01/1960 00:00:00", DateTimeFormatter.ofPattern(datePattern))
  private val ts1980 = LocalDateTime.parse("01/01/1980 00:00:00", DateTimeFormatter.ofPattern(datePattern))
  private val offset = OffsetDateTime.now.getOffset


  case class DataType(
      string: Option[String] = None,
      integer: Option[Int] = None,
      float: Option[Float] = None,
      double: Option[Double] = None,
      boolean: Option[Boolean] = None,
      date: String = timeStamp.toLocalDate.toString,
      time: String = timeStamp.toLocalTime.toString,
      dateTime: String = timeStamp.toString,
      `dateDaysSince[0]`: Long = Duration.between(yearZeroTimeStamp, timeStamp).toDays,
      `dateDaysSince[1960]`: Long = Duration.between(ts1960, timeStamp).toDays,
      `dateDaysSince[1970]`: Long = timeStamp.toLocalDate.toEpochDay,
      `dateDaysSince[1980]`: Long = Duration.between(ts1980, timeStamp).toDays,
      timeSeconds: Long = LocalDateTime.now().getSecond,
      `dateTimeSecondsSince[0]`: Long = Duration.between(yearZeroTimeStamp, timeStamp).toMillis / 1000,
      `dateTimeSecondsSince[1960]`: Long = Duration.between(ts1960, timeStamp).toMillis / 1000 ,
      `dateTimeSecondsSince[1970]`: Long = timeStamp.toEpochSecond(offset),
      `dateTimeSecondsSince[1980]`: Long = Duration.between(ts1980, timeStamp).toMillis / 1000
  )
}
