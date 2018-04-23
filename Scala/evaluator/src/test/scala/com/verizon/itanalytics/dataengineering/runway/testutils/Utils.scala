package com.verizon.itanalytics.dataengineering.runway.testutils

import scala.reflect.runtime.universe._

trait Utils {
  def classAccessors[T: TypeTag]: List[MethodSymbol] = typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }.toList
}
