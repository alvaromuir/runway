package com.verizon.itanalytics.dataengineering.runway.evaluator.models

import com.verizon.itanalytics.dataengineering.runway.evaluator.models.elements.NearestNeighborModel
import org.dmg.pmml.PMML

import scala.collection.JavaConverters._

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 05, 2018
 */

trait KNearestNeighbor extends NearestNeighborModel {

  def parseNearestNeighborModel(pMML: PMML): KNearestNeighbor = {
    val nearestNeighborModel = pMML
      .getModels
      .get(0)
      .asInstanceOf[org.dmg.pmml.nearest_neighbor.NearestNeighborModel]

    KNearestNeighbor(
      extension = nearestNeighborModel.getExtensions match {
        case null => None
        case _ =>
          Option(nearestNeighborModel.getExtensions.asScala.map {
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
                    Option(e.getContent.asScala.map { c =>
                      c.toString
                    })
                }
              )
          })
      },
      miningSchema = MiningSchema(miningFields =
        nearestNeighborModel.getMiningSchema.getMiningFields match {
          case null => None
          case _ =>
            Option(
              nearestNeighborModel.getMiningSchema.getMiningFields.asScala.map {
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
      output = nearestNeighborModel.getOutput match {
        case null => None
        case _ =>
          Option(
            Output(
              extension = nearestNeighborModel.getExtensions match {
                case null => None
                case _ =>
                  Option(nearestNeighborModel.getExtensions.asScala.map {
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
                            Option(e.getContent.asScala.map { c =>
                              c.toString
                            })
                        }
                      )
                  })
              },
              outputField =
                nearestNeighborModel.getOutput.getOutputFields.asScala
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
                  }
            ))
      },
      modelStats = nearestNeighborModel.getModelStats match {
        case null => None
        case _ =>
          Option(
            ModelStats(
              univariateStats =
                nearestNeighborModel.getModelStats.getUnivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getModelStats.getUnivariateStats.asScala
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
                nearestNeighborModel.getModelStats.getMultivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getModelStats.getMultivariateStats.asScala
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
      modelExplanation = nearestNeighborModel.getModelExplanation match {
        case null => None
        case _ =>
          Option(
            ModelExplanation(
              extension =
                nearestNeighborModel.getModelExplanation.getExtensions match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getModelExplanation.getExtensions.asScala
                        .map { e =>
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
              correlations =
                nearestNeighborModel.getModelExplanation.getCorrelations match {
                  case null => None
                  case _ =>
                    Option(
                      Correlations(
                        extension =
                          nearestNeighborModel.getModelExplanation.getCorrelations.getExtensions match {
                            case null => None
                            case _ =>
                              Option(
                                nearestNeighborModel.getModelExplanation.getCorrelations.getExtensions.asScala
                                  .map { e =>
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
                        correlationFields = Array(
                          n =
                            nearestNeighborModel.getModelExplanation.getCorrelations.getCorrelationFields.getArray.getN.toInt,
                          `type` =
                            nearestNeighborModel.getModelExplanation.getCorrelations.getCorrelationFields.getArray.getType
                              .value(),
                          value =
                            nearestNeighborModel.getModelExplanation.getCorrelations.getCorrelationFields.getArray.getValue
                        ),
                        correlationValues = {
                          val mtx =
                            nearestNeighborModel.getModelExplanation.getCorrelations.getCorrelationValues.getMatrix
                          Matrix(
                            matCell = mtx.getMatCells match {
                              case null => None
                              case _ =>
                                Option(mtx.getMatCells.asScala.map { c =>
                                  MatCell(row = c.getRow,
                                          col = c.getCol,
                                          value = c.getValue)
                                })
                            },
                            kind = mtx.getKind.value(),
                            nbCols = mtx.getNbCols match {
                              case null => None
                              case _    => Option(mtx.getNbCols)
                            },
                            nbRows = mtx.getNbRows match {
                              case null => None
                              case _    => Option(mtx.getNbRows)
                            },
                            diagDefault = mtx.getDiagDefault match {
                              case null => None
                              case _    => Option(mtx.getDiagDefault)
                            },
                            offDiagDefault = mtx.getOffDiagDefault match {
                              case null => None
                              case _    => Option(mtx.getOffDiagDefault)
                            }
                          )
                        },
                        correlationMethods =
                          nearestNeighborModel.getModelExplanation.getCorrelations.getCorrelationMethods match {
                            case null => None
                            case _ =>
                              val mtx =
                                nearestNeighborModel.getModelExplanation.getCorrelations.getCorrelationMethods.getMatrix
                              Option(
                                Matrix(
                                  matCell = mtx.getMatCells match {
                                    case null => None
                                    case _ =>
                                      Option(mtx.getMatCells.asScala.map { c =>
                                        MatCell(row = c.getRow,
                                                col = c.getCol,
                                                value = c.getValue)
                                      })
                                  },
                                  kind = mtx.getKind.value(),
                                  nbCols = mtx.getNbCols match {
                                    case null => None
                                    case _    => Option(mtx.getNbCols)
                                  },
                                  nbRows = mtx.getNbRows match {
                                    case null => None
                                    case _    => Option(mtx.getNbRows)
                                  },
                                  diagDefault = mtx.getDiagDefault match {
                                    case null => None
                                    case _    => Option(mtx.getDiagDefault)
                                  },
                                  offDiagDefault = mtx.getOffDiagDefault match {
                                    case null => None
                                    case _    => Option(mtx.getOffDiagDefault)
                                  }
                                ))
                          }
                      ))
                },
              predictiveModelQualities =
                nearestNeighborModel.getModelExplanation.getPredictiveModelQualities match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getModelExplanation.getPredictiveModelQualities.asScala
                        .map {
                          pmq =>
                            PredictiveModelQuality(
                              extension = pmq.getExtensions match {
                                case null => None
                                case _ =>
                                  Option(pmq.getExtensions.asScala.map { e =>
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
                              confusionMatrix = pmq.getConfusionMatrix match {
                                case null => None
                                case _ =>
                                  Option(ConfusionMatrix(
                                    extension = pmq.getExtensions match {
                                      case null => None
                                      case _ =>
                                        Option(pmq.getExtensions.asScala.map {
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
                                              }
                                            )
                                        })
                                    },
                                    classLabels =
                                      pmq.getConfusionMatrix.getClassLabels match {
                                        case null => None
                                        case _ =>
                                          Option(ClassLabels(
                                            extension =
                                              pmq.getExtensions match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    nearestNeighborModel.getModelExplanation.getExtensions.asScala
                                                      .map { e =>
                                                        Extension(
                                                          extender =
                                                            e.getExtender match {
                                                              case null => None
                                                              case _ =>
                                                                Option(
                                                                  e.getExtender)
                                                            },
                                                          name =
                                                            e.getName match {
                                                              case null => None
                                                              case _ =>
                                                                Option(
                                                                  e.getName)
                                                            },
                                                          value =
                                                            e.getValue match {
                                                              case null => None
                                                              case _ =>
                                                                Option(
                                                                  e.getValue)
                                                            }
                                                        )
                                                      })
                                              },
                                            labels = Array(
                                              n =
                                                pmq.getConfusionMatrix.getClassLabels.getArray.getN,
                                              `type` =
                                                pmq.getConfusionMatrix.getClassLabels.getArray.getType
                                                  .value(),
                                              value =
                                                pmq.getConfusionMatrix.getClassLabels.getArray.getValue
                                            )
                                          ))
                                      },
                                    matrix =
                                      pmq.getConfusionMatrix.getMatrix match {
                                        case null => None
                                        case _ =>
                                          Option(Matrix(
                                            extension =
                                              pmq.getConfusionMatrix.getExtensions match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    pmq.getConfusionMatrix.getExtensions.asScala
                                                      .map { e =>
                                                        Extension(
                                                          extender =
                                                            e.getExtender match {
                                                              case null => None
                                                              case _ =>
                                                                Option(
                                                                  e.getExtender)
                                                            },
                                                          name =
                                                            e.getName match {
                                                              case null => None
                                                              case _ =>
                                                                Option(
                                                                  e.getName)
                                                            },
                                                          value =
                                                            e.getValue match {
                                                              case null => None
                                                              case _ =>
                                                                Option(
                                                                  e.getValue)
                                                            }
                                                        )
                                                      })
                                              },
                                            matCell =
                                              pmq.getConfusionMatrix.getMatrix.getMatCells match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    pmq.getConfusionMatrix.getMatrix.getMatCells.asScala
                                                      .map { mc =>
                                                        MatCell(
                                                          row = mc.getRow,
                                                          col = mc.getCol,
                                                          value = mc.getValue
                                                        )
                                                      })
                                              },
                                            kind =
                                              pmq.getConfusionMatrix.getMatrix.getKind
                                                .value(),
                                            nbRows =
                                              pmq.getConfusionMatrix.getMatrix.getNbRows match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    pmq.getConfusionMatrix.getMatrix.getNbRows.toInt)
                                              },
                                            nbCols =
                                              pmq.getConfusionMatrix.getMatrix.getNbCols match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    pmq.getConfusionMatrix.getMatrix.getNbCols.toInt)
                                              },
                                            diagDefault =
                                              pmq.getConfusionMatrix.getMatrix.getDiagDefault match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    pmq.getConfusionMatrix.getMatrix.getDiagDefault.toDouble)
                                              },
                                            offDiagDefault =
                                              pmq.getConfusionMatrix.getMatrix.getOffDiagDefault match {
                                                case null => None
                                                case _ =>
                                                  Option(
                                                    pmq.getConfusionMatrix.getMatrix.getOffDiagDefault.toDouble)
                                              }
                                          ))
                                      }
                                  ))
                              },
                              liftData = pmq.getLiftDatas match {
                                case null => None
                                case _ =>
                                  Option(pmq.getLiftDatas.asScala.map {
                                    d =>
                                      LiftData(
                                        targetFieldValue =
                                          d.getTargetFieldValue match {
                                            case null => None
                                            case _ =>
                                              Option(d.getTargetFieldValue)
                                          },
                                        targetFieldDisplayValue =
                                          d.getTargetFieldDisplayValue match {
                                            case null => None
                                            case _ =>
                                              Option(
                                                d.getTargetFieldDisplayValue)
                                          },
                                        rankingQuality =
                                          d.getRankingQuality match {
                                            case null => None
                                            case _ =>
                                              Option(
                                                d.getRankingQuality.toDouble)
                                          },
                                        modelLiftGraph = LiftGraph(
                                          xCoordinates = Array(
                                            n =
                                              d.getModelLiftGraph.getLiftGraph.getXCoordinates.getArray.getN,
                                            `type` =
                                              d.getModelLiftGraph.getLiftGraph.getXCoordinates.getArray.getType
                                                .value(),
                                            value =
                                              d.getModelLiftGraph.getLiftGraph.getXCoordinates.getArray.getValue
                                          ),
                                          yCoordinates = Array(
                                            n =
                                              d.getModelLiftGraph.getLiftGraph.getYCoordinates.getArray.getN,
                                            `type` =
                                              d.getModelLiftGraph.getLiftGraph.getYCoordinates.getArray.getType
                                                .value(),
                                            value =
                                              d.getModelLiftGraph.getLiftGraph.getYCoordinates.getArray.getValue
                                          ),
                                          boundaryValues =
                                            d.getModelLiftGraph.getLiftGraph.getBoundaryValues match {
                                              case null => None
                                              case _ =>
                                                Option(Array(
                                                  n =
                                                    d.getModelLiftGraph.getLiftGraph.getBoundaryValues.getArray.getN,
                                                  `type` =
                                                    d.getModelLiftGraph.getLiftGraph.getBoundaryValues.getArray.getType
                                                      .value(),
                                                  value =
                                                    d.getModelLiftGraph.getLiftGraph.getBoundaryValues.getArray.getValue
                                                ))
                                            },
                                          boundaryValueMeans =
                                            d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans match {
                                              case null => None
                                              case _ =>
                                                Option(Array(
                                                  n =
                                                    d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getN,
                                                  `type` =
                                                    d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getType
                                                      .value(),
                                                  value =
                                                    d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getValue
                                                ))
                                            }
                                        ),
                                        optimumLiftGraph =
                                          d.getOptimumLiftGraph match {
                                            case null => None
                                            case _ =>
                                              Option(LiftGraph(
                                                xCoordinates = Array(
                                                  n =
                                                    d.getOptimumLiftGraph.getLiftGraph.getXCoordinates.getArray.getN,
                                                  `type` =
                                                    d.getOptimumLiftGraph.getLiftGraph.getXCoordinates.getArray.getType
                                                      .value(),
                                                  value =
                                                    d.getOptimumLiftGraph.getLiftGraph.getXCoordinates.getArray.getValue
                                                ),
                                                yCoordinates = Array(
                                                  n =
                                                    d.getOptimumLiftGraph.getLiftGraph.getYCoordinates.getArray.getN,
                                                  `type` =
                                                    d.getOptimumLiftGraph.getLiftGraph.getYCoordinates.getArray.getType
                                                      .value(),
                                                  value =
                                                    d.getOptimumLiftGraph.getLiftGraph.getYCoordinates.getArray.getValue
                                                ),
                                                boundaryValues =
                                                  d.getModelLiftGraph.getLiftGraph.getBoundaryValues match {
                                                    case null => None
                                                    case _ =>
                                                      Option(Array(
                                                        n =
                                                          d.getOptimumLiftGraph.getLiftGraph.getBoundaryValues.getArray.getN,
                                                        `type` =
                                                          d.getOptimumLiftGraph.getLiftGraph.getBoundaryValues.getArray.getType
                                                            .value(),
                                                        value =
                                                          d.getOptimumLiftGraph.getLiftGraph.getBoundaryValues.getArray.getValue
                                                      ))
                                                  },
                                                boundaryValueMeans =
                                                  d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans match {
                                                    case null => None
                                                    case _ =>
                                                      Option(Array(
                                                        n =
                                                          d.getOptimumLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getN,
                                                        `type` =
                                                          d.getOptimumLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getType
                                                            .value(),
                                                        value =
                                                          d.getOptimumLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getValue
                                                      ))
                                                  }
                                              ))
                                          },
                                        randomLiftGraph =
                                          d.getRandomLiftGraph match {
                                            case null => None
                                            case _ =>
                                              Option(LiftGraph(
                                                extension =
                                                  d.getModelLiftGraph.getLiftGraph.getExtensions match {
                                                    case null => None
                                                    case _ =>
                                                      Option(
                                                        d.getModelLiftGraph.getLiftGraph.getExtensions.asScala
                                                          .map { e =>
                                                            Extension(
                                                              extender =
                                                                e.getExtender match {
                                                                  case null =>
                                                                    None
                                                                  case _ =>
                                                                    Option(e.getExtender)
                                                                },
                                                              name =
                                                                e.getName match {
                                                                  case null =>
                                                                    None
                                                                  case _ =>
                                                                    Option(
                                                                      e.getName)
                                                                },
                                                              value =
                                                                e.getValue match {
                                                                  case null =>
                                                                    None
                                                                  case _ =>
                                                                    Option(
                                                                      e.getValue)
                                                                }
                                                            )
                                                          })
                                                  },
                                                xCoordinates = Array(
                                                  n =
                                                    d.getModelLiftGraph.getLiftGraph.getXCoordinates.getArray.getN,
                                                  `type` =
                                                    d.getModelLiftGraph.getLiftGraph.getXCoordinates.getArray.getType
                                                      .value(),
                                                  value =
                                                    d.getModelLiftGraph.getLiftGraph.getXCoordinates.getArray.getValue
                                                ),
                                                yCoordinates = Array(
                                                  n =
                                                    d.getModelLiftGraph.getLiftGraph.getYCoordinates.getArray.getN,
                                                  `type` =
                                                    d.getModelLiftGraph.getLiftGraph.getYCoordinates.getArray.getType
                                                      .value(),
                                                  value =
                                                    d.getModelLiftGraph.getLiftGraph.getYCoordinates.getArray.getValue
                                                ),
                                                boundaryValues =
                                                  d.getModelLiftGraph.getLiftGraph.getBoundaryValues match {
                                                    case null => None
                                                    case _ =>
                                                      Option(Array(
                                                        n =
                                                          d.getModelLiftGraph.getLiftGraph.getBoundaryValues.getArray.getN,
                                                        `type` =
                                                          d.getModelLiftGraph.getLiftGraph.getBoundaryValues.getArray.getType
                                                            .value(),
                                                        value =
                                                          d.getModelLiftGraph.getLiftGraph.getBoundaryValues.getArray.getValue
                                                      ))
                                                  },
                                                boundaryValueMeans =
                                                  d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans match {
                                                    case null => None
                                                    case _ =>
                                                      Option(Array(
                                                        n =
                                                          d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getN,
                                                        `type` =
                                                          d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getType
                                                            .value(),
                                                        value =
                                                          d.getModelLiftGraph.getLiftGraph.getBoundaryValueMeans.getArray.getValue
                                                      ))
                                                  }
                                              ))
                                          }
                                      )
                                  })
                              },
                              ROC = pmq.getROC match {
                                case null => None
                                case _ =>
                                  Option(ROC(
                                    extension = pmq.getROC.getExtensions match {
                                      case null => None
                                      case _ =>
                                        Option(
                                          pmq.getROC.getExtensions.asScala.map {
                                            e =>
                                              Extension(
                                                extender = e.getExtender match {
                                                  case null => None
                                                  case _ =>
                                                    Option(e.getExtender)
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
                                    positiveTargetFieldValue =
                                      pmq.getROC.getPositiveTargetFieldValue,
                                    positiveTargetFieldDisplayValue =
                                      pmq.getROC.getPositiveTargetFieldDisplayValue match {
                                        case null => None
                                        case _ =>
                                          Option(
                                            pmq.getROC.getPositiveTargetFieldDisplayValue)
                                      },
                                    negativeTargetFieldDisplayValue =
                                      pmq.getROC.getNegativeTargetFieldDisplayValue match {
                                        case null => None
                                        case _ =>
                                          Option(
                                            pmq.getROC.getNegativeTargetFieldDisplayValue)
                                      },
                                    negativeTargetFieldValue =
                                      pmq.getROC.getNegativeTargetFieldValue match {
                                        case null => None
                                        case _ =>
                                          Option(
                                            pmq.getROC.getNegativeTargetFieldValue)
                                      },
                                    rocGraph = pmq.getROC.getROCGraph match {
                                      case null => None
                                      case _ =>
                                        Option(ROCGraph(
                                          extension =
                                            pmq.getROC.getROCGraph.getExtensions match {
                                              case null => None
                                              case _ =>
                                                Option(
                                                  pmq.getROC.getROCGraph.getExtensions.asScala
                                                    .map { e =>
                                                      Extension(
                                                        extender =
                                                          e.getExtender match {
                                                            case null => None
                                                            case _ =>
                                                              Option(
                                                                e.getExtender)
                                                          },
                                                        name = e.getName match {
                                                          case null => None
                                                          case _ =>
                                                            Option(e.getName)
                                                        },
                                                        value =
                                                          e.getValue match {
                                                            case null => None
                                                            case _ =>
                                                              Option(e.getValue)
                                                          }
                                                      )
                                                    })
                                            },
                                          xCoordinates = Array(
                                            n =
                                              pmq.getROC.getROCGraph.getXCoordinates.getArray.getN,
                                            `type` =
                                              pmq.getROC.getROCGraph.getXCoordinates.getArray.getType
                                                .value(),
                                            value =
                                              pmq.getROC.getROCGraph.getXCoordinates.getArray.getValue
                                          ),
                                          yCoordinates = Array(
                                            n =
                                              pmq.getROC.getROCGraph.getYCoordinates.getArray.getN,
                                            `type` =
                                              pmq.getROC.getROCGraph.getYCoordinates.getArray.getType
                                                .value(),
                                            value =
                                              pmq.getROC.getROCGraph.getYCoordinates.getArray.getValue
                                          ),
                                          boundaryValues =
                                            pmq.getROC.getROCGraph.getBoundaryValues match {
                                              case null => None
                                              case _ =>
                                                Option(Array(
                                                  n =
                                                    pmq.getROC.getROCGraph.getBoundaryValues.getArray.getN,
                                                  `type` =
                                                    pmq.getROC.getROCGraph.getBoundaryValues.getArray.getType
                                                      .value(),
                                                  value =
                                                    pmq.getROC.getROCGraph.getBoundaryValues.getArray.getValue
                                                ))
                                            }
                                        ))
                                    }
                                  ))
                              },
                              targetField = pmq.getTargetField.getValue,
                              dataName = pmq.getDataName match {
                                case null => None
                                case _    => Option(pmq.getDataName)
                              },
                              dataUsage = pmq.getDataUsage.value(),
                              meanError = pmq.getMeanError match {
                                case null => None
                                case _    => Option(pmq.getMeanError.toDouble)
                              },
                              meanAbsoluteError =
                                pmq.getMeanAbsoluteError match {
                                  case null => None
                                  case _ =>
                                    Option(pmq.getMeanAbsoluteError.toDouble)
                                },
                              meanSquaredError = pmq.getMeanSquaredError match {
                                case null => None
                                case _ =>
                                  Option(pmq.getMeanSquaredError.toDouble)
                              },
                              rootMeanSquaredError =
                                pmq.getRootMeanSquaredError match {
                                  case null => None
                                  case _ =>
                                    Option(pmq.getRootMeanSquaredError.toDouble)
                                },
                              `r-squared` = pmq.getRSquared match {
                                case null => None
                                case _    => Option(pmq.getRSquared.toDouble)
                              },
                              `adj-r-squared` = pmq.getAdjRSquared match {
                                case null => None
                                case _    => Option(pmq.getAdjRSquared.toDouble)
                              },
                              sumSquaredError = pmq.getSumSquaredError match {
                                case null => None
                                case _ =>
                                  Option(pmq.getSumSquaredError.toDouble)
                              },
                              sumSquaredRegression =
                                pmq.getSumSquaredRegression match {
                                  case null => None
                                  case _ =>
                                    Option(pmq.getSumSquaredRegression.toDouble)
                                },
                              numOfRecords = pmq.getNumOfRecords match {
                                case null => None
                                case _    => Option(pmq.getNumOfRecords.toDouble)
                              },
                              numOfRecordsWeighted =
                                pmq.getNumOfRecordsWeighted match {
                                  case null => None
                                  case _ =>
                                    Option(pmq.getNumOfRecordsWeighted.toDouble)
                                },
                              numOfPredictors = pmq.getNumOfPredictors match {
                                case null => None
                                case _ =>
                                  Option(pmq.getNumOfPredictors.toDouble)
                              },
                              degreesOfFreedom = pmq.getDegreesOfFreedom match {
                                case null => None
                                case _ =>
                                  Option(pmq.getDegreesOfFreedom.toDouble)
                              },
                              fStatistic = pmq.getFStatistic match {
                                case null => None
                                case _    => Option(pmq.getFStatistic.toDouble)
                              },
                              AIC = pmq.getAIC match {
                                case null => None
                                case _    => Option(pmq.getAIC.toDouble)
                              },
                              BIC = pmq.getBIC match {
                                case null => None
                                case _    => Option(pmq.getBIC.toDouble)
                              },
                              AICc = pmq.getAICc match {
                                case null => None
                                case _    => Option(pmq.getAICc.toDouble)
                              }
                            )
                        })
                },
              clusteringModelQualities =
                nearestNeighborModel.getModelExplanation.getClusteringModelQualities match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getModelExplanation.getClusteringModelQualities.asScala
                        .map {
                          cmq =>
                            ClusteringModelQuality(
                              dataName = cmq.getDataName match {
                                case null => None
                                case _    => Option(cmq.getDataName)
                              },
                              SSE = cmq.getSSE match {
                                case null => None
                                case _    => Option(cmq.getSSE.toDouble)
                              },
                              SSB = cmq.getSSB match {
                                case null => None
                                case _    => Option(cmq.getSSB.toDouble)
                              }
                            )
                        })
                }
            ))
      },
      targets = nearestNeighborModel.getTargets match {
        case null => None
        case _ =>
          Option(nearestNeighborModel.getTargets.asScala.map {
            t =>
              Targets(
                field = t.getField match {
                  case null => None
                  case _    => Option(t.getField.getValue)
                },
                optype = t.getOpType match {
                  case null => None
                  case _    => Option(t.getOpType.value())
                },
                castInteger = t.getCastInteger match {
                  case null => None
                  case _    => Option(t.getCastInteger.value())
                },
                min = t.getMin match {
                  case null => None
                  case _    => Option(t.getMin.toDouble)
                },
                max = t.getMax match {
                  case null => None
                  case _    => Option(t.getMax.toDouble)
                },
                rescaleConstant = t.getRescaleConstant.toDouble,
                rescaleFactor = t.getRescaleFactor.toDouble,
                targetValues = None
              )
          })
      },
      localTransformations = nearestNeighborModel.getLocalTransformations match {
        case null => None
        case _ =>
          Option(
            LocalTransformation(
              extension =
                nearestNeighborModel.getLocalTransformations.getExtensions match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getLocalTransformations.getExtensions.asScala
                        .map { e =>
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
              derivedFields =
                nearestNeighborModel.getLocalTransformations.getDerivedFields match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getLocalTransformations.getDerivedFields.asScala
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
                }
            ))
      },
      trainingInstances = nearestNeighborModel.getTrainingInstances match {
        case null => None
        case _ =>
          Option(
            TrainingInstances(
              isTransformed =
                nearestNeighborModel.getTrainingInstances.isTransformed,
              recordCount =
                nearestNeighborModel.getTrainingInstances.getRecordCount match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getTrainingInstances.getRecordCount.toInt)
                },
              fieldCount =
                nearestNeighborModel.getTrainingInstances.getFieldCount match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getTrainingInstances.getFieldCount.toInt)
                },
              instanceFields =
                nearestNeighborModel.getTrainingInstances.getInstanceFields match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getTrainingInstances.getInstanceFields.asScala
                        .map { f =>
                          InstanceField(
                            field = f.getField.getValue,
                            key = Option(f.getKey.getValue),
                            column = Option(f.getColumn)
                          )
                        })
                },
              tableLocator =
                nearestNeighborModel.getTrainingInstances.getTableLocator match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getTrainingInstances.getTableLocator.toString) // not sure what this is
                },
              inlineTables =
                nearestNeighborModel.getTrainingInstances.getInlineTable match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getTrainingInstances.getInlineTable.getRows.asScala
                        .map(r => Row(row = r.toString)))
                }
            ))
      },
      comparisonMeasure = nearestNeighborModel.getComparisonMeasure match {
        case null => None
        case _ =>
          Option(
            ComparisonMeasure(
              extension =
                nearestNeighborModel.getComparisonMeasure.getExtensions match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getComparisonMeasure.getExtensions.asScala
                        .map {
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
              kind = nearestNeighborModel.getComparisonMeasure.getKind.value(),
              compareFunction =
                nearestNeighborModel.getComparisonMeasure.getCompareFunction
                  .value(),
              minimum =
                nearestNeighborModel.getComparisonMeasure.getMinimum match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getComparisonMeasure.getMinimum.toDouble)
                },
              maximum =
                nearestNeighborModel.getComparisonMeasure.getMaximum match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getComparisonMeasure.getMaximum.toDouble)
                },
              measure =
                nearestNeighborModel.getComparisonMeasure.getMeasure match {
                  case null => None
                  case _ =>
                    Option(
                      nearestNeighborModel.getComparisonMeasure.getMeasure.toString) // revist
                }
            ))
      },
      knnInputs = nearestNeighborModel.getKNNInputs match {
        case null => None
        case _    => None
      },
      modelVerification = nearestNeighborModel.getModelVerification match {
        case null => None
        case _ =>
          val v = nearestNeighborModel.getModelVerification
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
      },
      modelName = nearestNeighborModel.getModelName match {
        case null => None
        case _    => Option(nearestNeighborModel.getModelName)
      },
      functionName = nearestNeighborModel.getMiningFunction.value(),
      algorithmName = nearestNeighborModel.getAlgorithmName match {
        case null => None
        case _    => Option(nearestNeighborModel.getAlgorithmName)
      },
      numberOfNeighbors = nearestNeighborModel.getNumberOfNeighbors,
      continuousScoringMethod =
        nearestNeighborModel.getContinuousScoringMethod.value(),
      categoricalScoringMethod =
        nearestNeighborModel.getCategoricalScoringMethod.value(),
      instanceIdVariable = nearestNeighborModel.getInstanceIdVariable match {
        case null => None
        case _    => None
      },
      threshold = nearestNeighborModel.getThreshold.toDouble,
      isScorable = Option(nearestNeighborModel.isScorable)
    )
  }
}
