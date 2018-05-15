package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 05 14, 2018
*/


trait ScoreDistribution extends Extension {
  case class ScoreDistribution(extension: Option[Seq[Extension]] = None,
                               value: String,
                               recordCount: Double,
                               confidence: Option[Double] = None,
                               probability: Option[Double] = None)
}
