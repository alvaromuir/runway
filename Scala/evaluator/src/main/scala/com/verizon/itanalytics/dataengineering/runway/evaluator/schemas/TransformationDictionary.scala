package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
* Project: Runway
* Alvaro Muir, Verizon IT Analytics: Data Engineering
* 04 26, 2018
*/


// http://dmg.org/pmml/v4-3/Transformations.html
trait TransformationDictionary extends Statistics {
  case class TransformationDictionary(
      defineFunctions: Option[Seq[DefineFunction]] = None,
      derivedFields: Option[Seq[DerivedField]] = None)

  case class DefineFunction(name: String,
                            optype: String,
                            dataType: Option[String] = None,
                            key: Option[String] = None,
                            parameterFields: Option[Seq[ParameterField]])

  case class ParameterField(name: String,
                            optype: String,
                            dataType: Option[String] = None)

  case class Apply(function: String,
                   mapMissingTo: Option[String] = None,
                   defaultValue: Option[String] = None,
                   invalidValueTreatment: String = "returnInvalid")

  case class LocalTransformation(derivedFields: Option[Seq[DerivedField]])

  case class DerivedField(name: Option[String] = None,
                          displayName: String,
                          optype: String,
                          dataType: String,
                          intervals: Option[Seq[Interval]] = None,
                          values: Option[Seq[String]] = None)

  case class Constant(dataType: String)

  case class FieldRef(field: String, mapMissingTo: Option[String] = None)

  case class NormContinuous(
      mapMissingTo: Option[Int] = None,
      field: String,
      outliers: String = "asIs" // see outlier treatments in MiningSchema
  )
  case class LinearNorm(orig: Double, norm: Double)

  case class NormDiscrete(field: String,
                          value: String,
                          mapMissingTo: Option[Int] = None)

  case class Discretization(field: String,
                            mapMissingTo: Option[String] = None,
                            defaultValue: Option[String] = None,
                            dataType: Option[String] = None)

  case class DiscretizeBin(binValue: String, interval: Interval)
}
