package com.verizon.itanalytics.dataengineering.runway.evaluator.testutils

import java.io.File

import com.verizon.itanalytics.dataengineering.runway.evaluator.Utils

import scala.collection.mutable
import scala.io.Source
import scala.reflect.runtime.universe._
import scala.util.control.ControlThrowable

trait TestUtils extends Utils {

  def classAccessors[T: TypeTag]: List[MethodSymbol] =
    typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }.toList

  /** A BLOCKING iterator of line numbers in a fle to list of Maps
    *
    * @param file a file descriptor
    * @param lineNum desired line to return, defaults to the first
    * @param limit total lines to return, defaults to 1
    * @return returns a map of observation
    */
  def readDataFile(file: File,
                   lineNum: Int = 1,
                   limit: Int = 1): List[Map[Any, Any]] = {
    val rslts = mutable.ListBuffer[Map[Any, Any]]()

    try {
      val bufferedSource = Source.fromFile(file)
      val data = bufferedSource.getLines.toList
      val header = data.head.toString
        .replace("\"", "")
        .split(",")
        .toList
      val observations = data.tail
      try {
        val lines: Unit = observations
          .map { _.split(",").toList }
          .map { header zip _ }
          .map { Map(_: _*) }
          .map { _.toMap[Any, Any] }
          .foreach(rslts += _)

        bufferedSource.close
      } catch safely {
        case ex: ControlThrowable => throw ex
      } finally if (bufferedSource != null) bufferedSource.close()
    } catch safely { case ex: ControlThrowable => throw ex }

    rslts.toList.slice(lineNum - 1, lineNum - 1 + limit)
  }
}
