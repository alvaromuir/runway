package com.verizon.itanalytics.dataengineering.runway

import scala.util.control.ControlThrowable

trait Utils {
  // courtesy of sumologic: https://www.sumologic.com/blog/code/why-you-should-never-catch-throwable-in-scala/
  def safely[T](handler: PartialFunction[Throwable, T]): PartialFunction[Throwable, T] = {
    case ex: ControlThrowable => throw ex
    case ex: Throwable if handler.isDefinedAt(ex) => handler(ex)
    case ex: Throwable => throw ex // un-necessary, but for clarity
  }
}
