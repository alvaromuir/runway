package com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements

import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 10, 2018
 */

@deprecated(message = "This module is deprecated", since = "pMML v.4.1")
// http://dmg.org/pmml/v4-3/Text.html
trait TextModel
    extends Extension
    with MiningSchema
    with Output
    with ModelStats
    with ModelExplanation
    with Taxonomy
    with Targets
    with LocalTransformation
    with ModelVerification {

  case class TextModel(
      extension: Option[Seq[Extension]] = None,
      miningSchema: MiningSchema,
      output: Option[Output] = None,
      modelStats: Option[ModelStats] = None,
      modelExplanation: Option[ModelExplanation] = None,
      targets: Option[Iterable[Targets]] = None,
      localTransformations: Option[LocalTransformation] = None,
      textDictionary: TextDictionary,
      textCorpus: TextCorpus,
      documentTermMatrix: DocumentTermMatrix,
      textModelNormalization: Option[TextModelNormalization] = None,
      textModelSimiliarity: Option[TextModelSimiliarity] = None,
      modelVerification: Option[ModelVerification] = None,
      modelName: Option[String] = None,
      functionName: String,
      algorithmName: Option[String] = None,
      numberOfTerms: Int,
      numberOfDocuments: Int,
      isScorable: Option[Boolean]
  )

  case class TextDictionary(extension: Option[Seq[Extension]] = None,
                            taxonomy: Option[Taxonomy] = None)

  case class TextCorpus(extension: Option[Seq[Extension]] = None,
                        textDocument: Option[Iterable[TextDocument]] = None)

  case class TextDocument(extension: Option[Seq[Extension]] = None,
                          id: String,
                          name: Option[String] = None,
                          length: Option[Int] = None,
                          file: Option[String] = None)

  case class DocumentTermMatrix(extension: Option[Seq[Extension]] = None,
                                matrix: Matrix)

  case class TextModelNormalization(
      extension: Option[Seq[Extension]] = None,
      localTermWeights: Option[String] = Option("termFrequency"),
      globalTermWeights: Option[String] = Option("inverseDocumentFrequency"),
      documentNormalization: Option[String] = Option("none"))

  case class TextModelSimiliarity(extension: Option[Seq[Extension]] = None,
                                  similarityType: Option[String])
}
