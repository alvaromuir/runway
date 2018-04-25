package com.verizon.itanalytics.dataengineering.runway.evaluator

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.scalatest.FlatSpec

class BaselineModelSpec
    extends FlatSpec
    with Builder
    with TestUtils
    with Evaluator {

  "the evaluator" should
    "read Baseline models" in {
    val status = "not yet implemented, requires a test file"
    assert(status != null)
  }

}
