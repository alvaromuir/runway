package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements.SequenceModel
import org.dmg.pmml.PMML

import scala.collection.JavaConverters._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 12, 2018
 */

trait SequenceRules extends SequenceModel {

  /** Parses provided pMML file as an Association Model
    *
    * @param pMML a valid pMML file
    * @return AssociationModel
    */
  def parseSequenceModel(pMML: PMML): SequenceModel = {
    val model = pMML.getModels
      .get(0)
      .asInstanceOf[org.dmg.pmml.sequence.SequenceModel]

    SequenceModel(
      extension = model.getExtensions match {
        case null => None
        case _ =>
          Option(model.getExtensions.asScala.map {
            e =>
              Extension(
                extender = e.getExtender match {
                  case null => None
                  case _    => Option(e.getExtender)
                },
                name = e.getName match {
                  case null => None
                  case _    => Option(e.getName)
                },
                value = e.getValue match {
                  case null => None
                  case _    => Option(e.getValue)
                },
                content = e.getContent match {
                  case null => None
                  case _ =>
                    Option(e.getContent.asScala.map {
                      _.toString
                    })
                }
              )
          })
      },
      miningSchema = MiningSchema(
        miningFields = model.getMiningSchema.getMiningFields match {
          case null => None
          case _ =>
            Option(model.getMiningSchema.getMiningFields.asScala.map {
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
                  missingValueReplacement = f.getMissingValueReplacement match {
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
      modelStats = model.getModelStats match {
        case null => None
        case _ =>
          Option(
            ModelStats(
              univariateStats = model.getModelStats.getUnivariateStats match {
                case null => None
                case _ =>
                  Option(
                    model.getModelStats.getUnivariateStats.asScala
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
                                  missingFreq =
                                    Option(u.getCounts.getMissingFreq.toDouble),
                                  invalidFreq =
                                    Option(u.getCounts.getInvalidFreq.toDouble),
                                  cardinality =
                                    Option(u.getCounts.getCardinality.toInt)
                                ))
                            },
                            numericInfo = u.getNumericInfo match {
                              case null => None
                              case _ =>
                                Option(NumericInfo(
                                  minimum = Option(u.getNumericInfo.getMinimum),
                                  maximum = Option(u.getNumericInfo.getMaximum),
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
                model.getModelStats.getMultivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      model.getModelStats.getMultivariateStats.asScala
                        .map {
                          m =>
                            MultivariateStats(
                              targetCategory = m.getTargetCategory match {
                                case null => None
                                case _    => Option(m.getTargetCategory)
                              },
                              multivariateStats =
                                m.getMultivariateStats.asScala.map { s =>
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
                                    chiSquareValue = s.getChiSquareValue match {
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
      localTransformations = model.getLocalTransformations match {
        case null => None
        case _ =>
          Option(
            LocalTransformation(derivedFields =
              model.getLocalTransformations.getDerivedFields match {
                case null => None
                case _ =>
                  Option(
                    model.getLocalTransformations.getDerivedFields.asScala
                      .map { d =>
                        DerivedField(
                          name = d.getName match {
                            case null => None
                            case _    => Option(d.getName.getValue)
                          },
                          displayName = d.getDisplayName match {
                            case null => None
                            case _    => Option(d.getDisplayName)
                          },
                          optype = d.getOpType.value(),
                          dataType = d.getDataType.value()
                        )
                      })
              }))
      },
      constraints = model.getConstraints match {
        case null => None
        case _ => Option(Constraints(
          extension = model.getConstraints.getExtensions match {
            case null => None
            case _ =>
              Option(model.getConstraints.getExtensions.asScala.map {
                e =>
                  Extension(
                    extender = e.getExtender match {
                      case null => None
                      case _    => Option(e.getExtender)
                    },
                    name = e.getName match {
                      case null => None
                      case _    => Option(e.getName)
                    },
                    value = e.getValue match {
                      case null => None
                      case _    => Option(e.getValue)
                    },
                    content = e.getContent match {
                      case null => None
                      case _ =>
                        Option(e.getContent.asScala.map {
                          _.toString
                        })
                    }
                  )
              })
          },
          minimumNumberOfItems = model.getConstraints.getMinimumNumberOfItems.toInt,
          maximumNumberOfItems = model.getConstraints.getMaximumNumberOfItems match {
            case null => None
            case _ => Option(model.getConstraints.getMaximumNumberOfItems.toInt)
          },
          minimumNumberOfAntecedentItems = model.getConstraints.getMinimumNumberOfAntecedentItems.toInt,
          maximumNumberOfAntecedentItems = model.getConstraints.getMaximumNumberOfAntecedentItems match {
            case null => None
            case _ => Option(model.getConstraints.getMaximumNumberOfAntecedentItems.toInt)
          },
          minimumNumberOfConsequentItems = model.getConstraints.getMinimumNumberOfConsequentItems.toInt,
          maximumNumberOfConsequentItems = model.getConstraints.getMaximumNumberOfConsequentItems match {
            case null => None
            case _ => Option(model.getConstraints.getMaximumNumberOfConsequentItems.toInt)
          },
          minimumSupport = model.getConstraints.getMinimumSupport.toDouble,
          minimumConfidence = model.getConstraints.getMinimumConfidence.toDouble,
          minimumLift = model.getConstraints.getMinimumLift.toDouble,
          minimumTotalSequenceTime = model.getConstraints.getMinimumTotalSequenceTime.toDouble,
          maximumTotalSequenceTime = model.getConstraints.getMaximumTotalSequenceTime match {
            case null => None
            case _ => Option(model.getConstraints.getMaximumTotalSequenceTime.toDouble)
          },
          minimumItemsetSeparationTime = model.getConstraints.getMinimumItemsetSeparationTime.toDouble,
          maximumItemsetSeparationTime = model.getConstraints.getMaximumItemsetSeparationTime match {
            case null => None
            case _ => Option(model.getConstraints.getMaximumItemsetSeparationTime.toDouble)
          },
          minimumAntConsSeparationTime = model.getConstraints.getMinimumAntConsSeparationTime.toDouble,
          maximumAntConsSeparationTime = model.getConstraints.getMaximumAntConsSeparationTime match {
            case null => None
            case _ => Option(model.getConstraints.getMaximumAntConsSeparationTime.toDouble)
          }
        ))
      },
      item = model.getItems match {
        case null => None
        case _ =>
          Option(
            model.getItems.asScala
              .map {
                i =>
                  Item(
                    extension = i.getExtensions match {
                      case null => None
                      case _ =>
                        Option(i.getExtensions.asScala.map { e =>
                          Extension(
                            extender = e.getExtender match {
                              case null => None
                              case _    => Option(e.getExtender)
                            },
                            name = e.getName match {
                              case null => None
                              case _    => Option(e.getName)
                            },
                            value = e.getValue match {
                              case null => None
                              case _    => Option(e.getValue)
                            }
                          )
                        })
                    },
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
      itemSet = model.getItemsets match {
        case null => None
        case _ =>
          Option(
            model.getItemsets.asScala
              .map {
                s =>
                  ItemSet(
                    extension = s.getExtensions match {
                      case null => None
                      case _ =>
                        Option(s.getExtensions.asScala.map { e =>
                          Extension(
                            extender = e.getExtender match {
                              case null => None
                              case _    => Option(e.getExtender)
                            },
                            name = e.getName match {
                              case null => None
                              case _    => Option(e.getName)
                            },
                            value = e.getValue match {
                              case null => None
                              case _    => Option(e.getValue)
                            }
                          )
                        })
                    },
                    id = s.getId,
                    support = s.getSupport match {
                      case null => None
                      case _    => Option(s.getSupport.toDouble)
                    },
                    numberOfItems = Option(s.getNumberOfItems.toInt),
                    itemRefs = s.getItemRefs.asScala.map {
                      r =>
                        ItemRef(
                          extension = r.getExtensions match {
                            case null => None
                            case _ =>
                              Option(r.getExtensions.asScala.map { e =>
                                Extension(
                                  extender = e.getExtender match {
                                    case null => None
                                    case _    => Option(e.getExtender)
                                  },
                                  name = e.getName match {
                                    case null => None
                                    case _    => Option(e.getName)
                                  },
                                  value = e.getValue match {
                                    case null => None
                                    case _    => Option(e.getValue)
                                  }
                                )
                              })
                          },
                          itemRef = r.getItemRef
                        )
                    }
                  )
              })
      },
      setPredicate = model.getSetPredicates match {
        case null => None
        case _ => None
      },
      sequence = model.getSequences.asScala.map {
        s => Sequence(
          extension = s.getExtensions match {
            case null => None
            case _ =>
              Option(s.getExtensions.asScala.map {
                e =>
                  Extension(
                    extender = e.getExtender match {
                      case null => None
                      case _    => Option(e.getExtender)
                    },
                    name = e.getName match {
                      case null => None
                      case _    => Option(e.getName)
                    },
                    value = e.getValue match {
                      case null => None
                      case _    => Option(e.getValue)
                    },
                    content = e.getContent match {
                      case null => None
                      case _ =>
                        Option(e.getContent.asScala.map {
                          _.toString
                        })
                    }
                  )
              })
          },
          setReference = SetReference(
            extension = s.getSetReference.getExtensions match {
              case null => None
              case _ =>
                Option(s.getSetReference.getExtensions.asScala.map {
                  e =>
                    Extension(
                      extender = e.getExtender match {
                        case null => None
                        case _    => Option(e.getExtender)
                      },
                      name = e.getName match {
                        case null => None
                        case _    => Option(e.getName)
                      },
                      value = e.getValue match {
                        case null => None
                        case _    => Option(e.getValue)
                      },
                      content = e.getContent match {
                        case null => None
                        case _ =>
                          Option(e.getContent.asScala.map {
                            _.toString
                          })
                      }
                    )
                })
            },
            setId = s.getSetReference.getSetId
          ),
          id = s.getId,
          key = s.getKey match {
            case null => None
            case _ => Option(s.getKey)
          },
          time = s.getTime match {
            case null => None
            case _ => Option(Time(
              extension = s.getTime.getExtensions match {
                case null => None
                case _ =>
                  Option(s.getTime.getExtensions.asScala.map {
                    e =>
                      Extension(
                        extender = e.getExtender match {
                          case null => None
                          case _    => Option(e.getExtender)
                        },
                        name = e.getName match {
                          case null => None
                          case _    => Option(e.getName)
                        },
                        value = e.getValue match {
                          case null => None
                          case _    => Option(e.getValue)
                        },
                        content = e.getContent match {
                          case null => None
                          case _ =>
                            Option(e.getContent.asScala.map {
                              _.toString
                            })
                        }
                      )
                  })
              },
              min = s.getTime.getMin match {
                case null => None
                case _ => Option(s.getTime.getMin.toDouble)
              },
              max = s.getTime.getMax match {
                case null => None
                case _ => Option(s.getTime.getMax.toDouble)
              },
              mean = s.getTime.getMean match {
                case null => None
                case _ => Option(s.getTime.getMean.toDouble)
              },
              standardDeviation = s.getTime.getStandardDeviation match {
                case null => None
                case _ => Option(s.getTime.getStandardDeviation.toDouble)
            }
            ))
          },
          content = s.getContent match {
            case null => None
            case _ => Option(s.getContent.asScala.map {
              c =>
                Content(
                  locator = c.getLocator match {
                    case null => None
                    case _ => Option(Locator(
                      publicId = c.getLocator.getPublicId match {
                        case null => None
                        case _ => Option(c.getLocator.getPublicId)
                      },
                      systemId = c.getLocator.getSystemId match {
                        case null => None
                        case _ => Option(c.getLocator.getSystemId)
                      },
                      lineNumber = c.getLocator.getLineNumber,
                      columnNumber = c.getLocator.getColumnNumber
                    ))
                  })
            })
          },
          numberOfSets = s.getNumberOfSets match {
            case null => None
            case _ => Option(s.getNumberOfSets.toInt)
          },
          occurrence = s.getOccurrence match {
            case null => None
            case _ => Option(s.getOccurrence.toInt)
          },
          support = s.getSupport match {
            case null => None
            case _ => Option(s.getSupport.toDouble)
          }
        )
      },
      sequenceRule = model.getSequenceRules match {
        case null => None
        case _ => None
      },
      modelName = model.getModelName match {
        case null => None
        case _    => Option(model.getModelName)
      },
      functionName = model.getMiningFunction.value(),
      algorithmName = model.getAlgorithmName match {
        case null => None
        case _    => Option(model.getAlgorithmName)
      },
      numberOfTransactions = model.getNumberOfTransactions match {
        case null => None
        case _ => Option(model.getNumberOfTransactions.toInt)
      },
      maxNumberOfItemsPerTransaction = model.getMaxNumberOfItemsPerTransaction match {
        case null => None
        case _ => Option(model.getMaxNumberOfItemsPerTransaction.toInt)
      },
      avgNumberOfItemsPerTransaction = model.getAvgNumberOfItemsPerTransaction match {
        case null => None
        case _ => Option(model.getAvgNumberOfItemsPerTransaction.toDouble)
      },
      numberOfTransactionGroups = model.getNumberOfTransactionGroups match {
        case null => None
        case _ => Option(model.getNumberOfTransactionGroups.toInt)
      },
      maxNumberOfTAsPerTAGroup = model.getMaxNumberOfTAsPerTAGroup match {
        case null => None
        case _ => Option(model.getMaxNumberOfTAsPerTAGroup.toInt)
      },
      avgNumberOfTAsPerTAGroup = model.getAvgNumberOfTAsPerTAGroup match {
        case null => None
        case _ => Option(model.getAvgNumberOfTAsPerTAGroup.toDouble)
      },
      isScorable = Option(model.isScorable)
    )
  }
}
