package com.verizon.itanalytics.dataengineering.runway.evaluator.parsers

import com.verizon.itanalytics.dataengineering.runway.evaluator.models.AssociationModel
import org.dmg.pmml.PMML

import scala.collection.JavaConverters._

trait AssociationModelParser extends AssociationModel {

  /** Parses provided pMML file as an Association Model
    *
    * @param pMML a valid pMML file
    * @return AssociationModel Schema
    */
  def parseAssociationModel(pMML: PMML): AssociationModel = {
    val associationModel = pMML.getModels
      .get(0)
      .asInstanceOf[org.dmg.pmml.association.AssociationModel]

    AssociationModel(
      modelName = associationModel.getModelName match {
        case null => None
        case _    => Option(associationModel.getModelName)
      },
      functionName = associationModel.getMiningFunction.value(),
      algorithmName = associationModel.getAlgorithmName match {
        case null => None
        case _ => Option(associationModel.getAlgorithmName)
      },
      numberOfTransactions = associationModel.getNumberOfTransactions,
      maxNumberOfItemsPerTA = associationModel.getAvgNumberOfItemsPerTA match {
        case null => None
        case _    => Option(associationModel.getAvgNumberOfItemsPerTA.toInt)
      },
      avgNumberOfItemsPerTA = associationModel.getAvgNumberOfItemsPerTA match {
        case null => None
        case _    => Option(associationModel.getAvgNumberOfItemsPerTA.toDouble)
      },
      minimumSupport = associationModel.getMinimumSupport,
      minimumConfidence = associationModel.getMinimumConfidence,
      lengthLimit = associationModel.getLengthLimit match {
        case null => None
        case _    => Option(associationModel.getLengthLimit)
      },
      numberOfItems = associationModel.getNumberOfItems,
      numberOfItemsets = associationModel.getNumberOfItemsets,
      numberOfRules = associationModel.getNumberOfRules,
      isScorable = Option(associationModel.isScorable),
      miningSchema = MiningSchema(
        miningFields = associationModel.getMiningSchema.getMiningFields match {
          case null => None
          case _ =>
            Option(
              associationModel.getMiningSchema.getMiningFields.asScala.map {
                f =>
                  MiningField(
                    name = f.getName.getValue,
                    usageType = f.getUsageType.value(),
                    optype = f.getOpType match {
                      case null => None
                      case _    => Option(f.getOpType.value())
                    },
                    importance = f.getImportance match {
                      case null => None
                      case _    => Option(f.getImportance.toDouble)
                    },
                    outliers = f.getOutlierTreatment.value(),
                    lowValue = f.getLowValue match {
                      case null => None
                      case _    => Option(f.getLowValue.toDouble)
                    },
                    highValue = f.getHighValue match {
                      case null => None
                      case _    => Option(f.getHighValue.toDouble)
                    },
                    missingValueReplacement =
                      f.getMissingValueReplacement match {
                        case null => None
                        case _    => Option(f.getMissingValueReplacement)
                      },
                    missingValueTreatment = f.getMissingValueTreatment match {
                      case null => None
                      case _    => Option(f.getMissingValueTreatment.value())
                    },
                    invalidValueTreatment = f.getInvalidValueTreatment.value()
                  )
              })
        }),
      output = associationModel.getOutput match {
        case null => None
        case _ =>
          Option(
            Output(
              outputFields = associationModel.getOutput.getOutputFields.asScala
                .map {
                  o =>
                    OutputField(
                      name = o.getName.getValue,
                      displayName = Option(o.getDisplayName),
                      optype = o.getOpType.value(),
                      targetField = Option(o.getTargetField.getValue),
                      feature = o.getResultFeature.value(),
                      value = Option(o.getValue),
                      ruleFeature = o.getRuleFeature.value(),
                      algorithm = o.getAlgorithm.value(),
                      rank = o.getRank.toInt,
                      rankBasis = o.getRankBasis.value(),
                      rankOrder = o.getRankOrder.value(),
                      isMultiValued = o.getIsMultiValued,
                      segmentId = Option(o.getSegmentId),
                      isFinalResult = o.isFinalResult
                    )
                }))
      },
      modelStats = associationModel.getModelStats match {
        case null => None
        case _ =>
          Option(
            ModelStats(
              univariateStats =
                associationModel.getModelStats.getUnivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      associationModel.getModelStats.getUnivariateStats.asScala
                        .map {
                          u =>
                            UnivariateStats(
                              field = u.getField.getValue,
                              weighted = u.getWeighted.value(),
                              counts = u.getCounts match {
                                case null => None
                                case _ =>
                                  Option(Counts(
                                    totalFreq = u.getCounts.getTotalFreq,
                                    missingFreq = Option(
                                      u.getCounts.getMissingFreq.toDouble),
                                    invalidFreq = Option(
                                      u.getCounts.getInvalidFreq.toDouble),
                                    cardinality =
                                      Option(u.getCounts.getCardinality.toInt)
                                  ))
                              },
                              numericInfo = u.getNumericInfo match {
                                case null => None
                                case _ =>
                                  Option(NumericInfo(
                                    minimum =
                                      Option(u.getNumericInfo.getMinimum),
                                    maximum =
                                      Option(u.getNumericInfo.getMaximum),
                                    mean = Option(u.getNumericInfo.getMean),
                                    standardDeviation = Option(
                                      u.getNumericInfo.getStandardDeviation),
                                    median = Option(u.getNumericInfo.getMedian),
                                    interQuartileRange = Option(
                                      u.getNumericInfo.getInterQuartileRange)
                                  ))
                              },
                              discStats = u.getDiscrStats match {
                                case null => None
                                case _ =>
                                  Option(
                                    DiscStats(
                                      arrays =
                                        Option(u.getDiscrStats.getArrays.asScala
                                          .map {
                                            _.toString
                                          }),
                                      modalValue =
                                        Option(u.getDiscrStats.getModalValue)
                                    ))
                              },
                              contStats = u.getContStats match {
                                case null => None
                                case _ =>
                                  Option(ContStats(
                                    intervals =
                                      u.getContStats.getIntervals match {
                                        case null => None
                                        case _ =>
                                          Option(
                                            u.getContStats.getIntervals.asScala
                                              .map { i =>
                                                Interval(
                                                  closure = i.getClosure.value(),
                                                  leftMargin =
                                                    Option(i.getLeftMargin),
                                                  rightMargin =
                                                    Option(i.getRightMargin)
                                                )
                                              })
                                      },
                                    totalValueSom =
                                      u.getContStats.getTotalValuesSum,
                                    totalSquaresSum =
                                      u.getContStats.getTotalSquaresSum
                                  ))
                              },
                              anova = u.getAnova match {
                                case null => None
                                case _ =>
                                  Option(Anova(
                                    target = u.getAnova.getTarget.getValue,
                                    anovaRow = u.getAnova.getAnovaRows match {
                                      case null => None
                                      case _ =>
                                        Option(
                                          u.getAnova.getAnovaRows.asScala.map {
                                            r =>
                                              AnovaRow(
                                                `type` = r.getType.value(),
                                                sumOfSquares = r.getSumOfSquares,
                                                degreesOfFreedom =
                                                  r.getDegreesOfFreedom,
                                                meanOfSquares =
                                                  r.getMeanOfSquares match {
                                                    case null => None
                                                    case _ =>
                                                      Option(r.getMeanOfSquares)
                                                  },
                                                fValue = r.getFValue match {
                                                  case null => None
                                                  case _    => Option(r.getFValue)
                                                },
                                                pValue = r.getPValue match {
                                                  case null => None
                                                  case _    => Option(r.getPValue)
                                                }
                                              )
                                          })
                                    }
                                  ))
                              }
                            )
                        })
                },
              multivariateStats =
                associationModel.getModelStats.getMultivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      associationModel.getModelStats.getMultivariateStats.asScala
                        .map {
                          m =>
                            MultivariateStats(
                              targetCategory = m.getTargetCategory match {
                                case null => None
                                case _    => Option(m.getTargetCategory)
                              },
                              multivariateStats =
                                m.getMultivariateStats.asScala.map {
                                  s =>
                                    MultivariateStat(
                                      name = s.getName,
                                      category = s.getCategory match {
                                        case null => None
                                        case _    => Option(s.getCategory)
                                      },
                                      exponent = s.getExponent.toInt,
                                      isIntercept = s.isIntercept,
                                      importance = s.getImportance match {
                                        case null => None
                                        case _ =>
                                          Option(s.getImportance.toDouble)
                                      },
                                      stdError = s.getStdError match {
                                        case null => None
                                        case _    => Option(s.getStdError.toDouble)
                                      },
                                      tValue = s.getTValue match {
                                        case null => None
                                        case _    => Option(s.getTValue.toDouble)
                                      },
                                      chiSquareValue =
                                        s.getChiSquareValue match {
                                          case null => None
                                          case _ =>
                                            Option(s.getChiSquareValue.toDouble)
                                        },
                                      fStatistic = s.getFStatistic match {
                                        case null => None
                                        case _ =>
                                          Option(s.getFStatistic.toDouble)
                                      },
                                      dF = s.getDF match {
                                        case null => None
                                        case _    => Option(s.getDF.toDouble)
                                      },
                                      pValueAlpha = s.getPValueAlpha match {
                                        case null => None
                                        case _ =>
                                          Option(s.getPValueAlpha.toDouble)
                                      },
                                      pValueInitial = s.getPValueInitial match {
                                        case null => None
                                        case _ =>
                                          Option(s.getPValueInitial.toDouble)
                                      },
                                      pValueFinal = s.getPValueFinal match {
                                        case null => None
                                        case _ =>
                                          Option(s.getPValueFinal.toDouble)
                                      },
                                      confidenceLevel =
                                        s.getConfidenceLevel.toDouble,
                                      confidenceLowerBound =
                                        s.getConfidenceLowerBound match {
                                          case null => None
                                          case _ =>
                                            Option(
                                              s.getConfidenceLowerBound.toDouble)
                                        },
                                      confidenceUpperBound =
                                        s.getConfidenceUpperBound match {
                                          case null => None
                                          case _ =>
                                            Option(
                                              s.getConfidenceUpperBound.toDouble)
                                        }
                                    )
                                }
                            )
                        })
                }
            ))
      },
      localTransformation = associationModel.getLocalTransformations match {
        case null => None
        case _ =>
          Option(
            LocalTransformation(derivedFields =
              associationModel.getLocalTransformations.getDerivedFields match {
                case null => None
                case _ =>
                  Option(
                    associationModel.getLocalTransformations.getDerivedFields.asScala
                      .map { d =>
                        DerivedField(
                          name = d.getName match {
                            case null => None
                            case _    => Option(d.getName.getValue)
                          },
                          displayName = d.getDisplayName,
                          optype = d.getOpType.value(),
                          dataType = d.getDataType.value()
                        )
                      })
              }))
      },
      items = associationModel.getItems match {
        case null => None
        case _ =>
          Option(
            associationModel.getItems.asScala
              .map {
                i =>
                  Item(
                    id = i.getId,
                    value = i.getValue,
                    field = i.getField match {
                      case null => None
                      case _    => Option(i.getField.getValue)
                    },
                    category = Option(i.getCategory),
                    mappedValue = Option(i.getMappedValue),
                    weight = i.getWeight match {
                      case null => None
                      case _    => Option(i.getWeight.toDouble)
                    }
                  )
              })
      },
      itemSets = associationModel.getItemsets match {
        case null => None
        case _ =>
          Option(
            associationModel.getItemsets.asScala
              .map { s =>
                ItemSet(
                  id = s.getId,
                  support = s.getSupport match {
                    case null => None
                    case _    => Option(s.getSupport.toDouble)
                  },
                  numberOfItems = Option(s.getNumberOfItems.toInt),
                  itemRefs = s.getItemRefs.asScala.map { r =>
                    ItemRef(r.getItemRef)
                  }
                )
              })
      },
      associationRules = associationModel.getAssociationRules match {
        case null => None
        case _ =>
          Option(
            associationModel.getAssociationRules.asScala
              .map {
                a =>
                  AssociationRule(
                    antecedent = a.getAntecedent,
                    consequent = a.getConsequent,
                    support = a.getSupport,
                    confidence = a.getConfidence,
                    lift = a.getLift match {
                      case null => None
                      case _    => Option(a.getLift)
                    },
                    leverage = a.getLeverage match {
                      case null => None
                      case _    => Option(a.getLeverage.toDouble)
                    },
                    affinity = a.getAffinity match {
                      case null => None
                      case _    => Option(a.getAffinity.toDouble)
                    },
                    id = Option(a.getId)
                  )
              })
      },
      modelVerification = associationModel.getModelVerification match {
        case null => None
        case _ =>
          val v = associationModel.getModelVerification
          Option(
            ModelVerification(
              recordCount = v.getRecordCount match {
                case null => None
                case _    => Option(v.getRecordCount.toInt)
              },
              fieldCount = v.getFieldCount match {
                case null => None
                case _    => Option(v.getFieldCount.toInt)
              },
              verificationFields = None
            ))
      }
    )
  }
}
