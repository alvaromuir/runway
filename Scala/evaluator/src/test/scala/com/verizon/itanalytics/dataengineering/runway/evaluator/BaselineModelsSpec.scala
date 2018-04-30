package com.verizon.itanalytics.dataengineering.runway.evaluator

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 25, 2018
*/

import com.verizon.itanalytics.dataengineering.runway.evaluator.testutils.TestUtils
import org.scalatest.FlatSpec

class BaselineModelsSpec
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
