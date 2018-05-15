package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 14, 2018
 */

trait EmbeddedModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with Targets
    with LocalTransformation {

  case class EmbeddedModel(extension: Option[Seq[Extension]] = None,
                           output: Option[Output] = None,
                           modelStats: Option[ModelStats] = None,
                           targets: Option[Iterable[Targets]] = None,
                           localTransformations: Option[LocalTransformation] =
                             None,
                           modelName: Option[String] = None,
                           functionName: String)
}
