package com.verizon.itanalytics.dataengineering.runway.evaluator.schemas

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 30, 2018
 */

trait Expression extends Taxonomy {

  case class Expression(
      constant: Option[Constant] = None,
      fieldRef: Option[FieldRef] = None,
      normContinuous: Option[NormContinuous] = None,
      normDiscrete: Option[NormDiscrete] = None,
      discretize: Option[Discretize] = None,
      mapValues: Option[MapValues] = None,
      textIndex: Option[TextIndex] = None,
      apply: Option[Apply] = None,
      aggregate: Option[Aggregate] = None,
      lag: Option[Lag] = None
  )

  case class Constant(
      dataType: DataType
  )

  case class FieldRef(
      extension: Option[Seq[Extension]] = None,
      field: String,
      mapMissingTo: Option[String] = None
  )

  case class NormContinuous(extension: Option[Seq[Extension]] = None,
                            linearNorm: Seq[LinearNorm],
                            mapMissingTo: Double,
                            field: String,
                            outlierTreatmentMethod: String = "asIs")

  case class LinearNorm(
      extension: Option[Seq[Extension]] = None,
      orig: Double,
      norm: Double
  )

  case class NormDiscrete(extension: Option[Seq[Extension]] = None,
                          field: String,
                          value: String,
                          mapMissingTo: Double)

  case class Discretize(extension: Option[Seq[Extension]] = None,
                        discretizeBin: Option[DiscretizeBin] = None,
                        field: String,
                        mapMissingTo: Option[String] = None,
                        defaultValue: Option[String] = None,
                        dataType: DataType)

  case class DiscretizeBin(extension: Option[Seq[Extension]] = None,
                           binValue: String)

  case class MapValues(extension: Option[Seq[Extension]] = None,
                       fieldColumnPair: Option[Seq[FieldColumnPair]] = None,
                       tableLocator: Option[TableLocator] = None,
                       inlineTable: Option[InlineTable] = None,
                       mapMissingTo: Option[String] = None,
                       defaultValue: Option[String] = None,
                       outputColumn: String,
                       dataType: DataType)

  case class FieldColumnPair(extension: Option[Seq[Extension]] = None,
                             field: String,
                             column: String)

  case class TextIndex(
      extension: Option[Seq[Extension]] = None,
      textIndexNormalization: Option[Seq[TextIndexNormalization]] = None,
      textField: String,
      localTermWeights: String = "termFrequency",
      isCaseSensitive: Boolean = false,
      maxLevenshteinDistance: Int = 0,
      countHits: String = "allHits",
      wordSeparatorCharacterRE: String = "\\s",
      tokenize: Boolean = true)

  case class TextIndexNormalization(extension: Option[Seq[Extension]] = None,
                                    tableLocator: Option[TableLocator] = None,
                                    inlineTable: Option[InlineTable] = None,
                                    infield: String = "string",
                                    outField: String = "stem",
                                    regexField: String = "regex",
                                    recursive: Boolean = false,
                                    isCaseSensitive: Boolean,
                                    maxLevenshteinDistance: Int,
                                    wordSeparatorCharacterRE: String,
                                    tokenize: Boolean)

  case class Apply(extension: Option[Seq[Extension]] = None,
                   function: String,
                   mapMissingTo: Option[String] = None,
                   defaultValue: Option[String] = None,
                   invalidValueTreatment: String = "returnInvalid")

  case class Aggregate(extension: Option[Seq[Extension]] = None,
                       field: String,
                       function: String,
                       groupField: Option[String] = None,
                       sqlWhere: Option[String] = None)

  case class Lag(extension: Option[Seq[Extension]] = None,
                 blockIndicator: Option[Seq[BlockIndicator]] = None,
                 field: String,
                 n: Int = 1)

  case class BlockIndicator(
      field: String
  )

}
