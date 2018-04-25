package com.verizon.itanalytics.dataengineering.runway.evaluator.parsers

import com.verizon.itanalytics.dataengineering.runway.evaluator.models.ClusterModels
import org.dmg.pmml.PMML

import scala.collection.JavaConverters._

trait ClusterModelsParser extends ClusterModels {

  def parseClusteringModel(pMML: PMML): ClusteringModel = {
    val clusteringModel = pMML.getModels
      .get(0)
      .asInstanceOf[org.dmg.pmml.clustering.ClusteringModel]

    ClusteringModel(
      modelName = clusteringModel.getModelName match {
        case null => None
        case _    => Option(clusteringModel.getModelName)
      },
      functionName = clusteringModel.getMiningFunction.value(),
      algorithmName = clusteringModel.getAlgorithmName match {
        case null => None
        case _ => Option(clusteringModel.getAlgorithmName)
      },
      modelClass = clusteringModel.getModelClass.value(),
      numberOfClusters = clusteringModel.getNumberOfClusters,
      isScorable = Option(clusteringModel.isScorable),
      miningSchema = MiningSchema(
        miningFields = clusteringModel.getMiningSchema.getMiningFields match {
          case null => None
          case _ =>
            Option(
              clusteringModel.getMiningSchema.getMiningFields.asScala.map {
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
      output = clusteringModel.getOutput match {
        case null => None
        case _ =>
          Option(
            Output(
              outputFields = clusteringModel.getOutput.getOutputFields.asScala
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
      modelStats = clusteringModel.getModelStats match {
        case null => None
        case _ =>
          Option(
            ModelStats(
              univariateStats =
                clusteringModel.getModelStats.getUnivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      clusteringModel.getModelStats.getUnivariateStats.asScala
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
                clusteringModel.getModelStats.getMultivariateStats match {
                  case null => None
                  case _ =>
                    Option(
                      clusteringModel.getModelStats.getMultivariateStats.asScala
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
      modelExplanation = clusteringModel.getModelExplanation match {
        case null => None
        case _ =>
          val me = clusteringModel.getModelExplanation
          Option(
            ModelExplanation(
              correlations = me.getCorrelations match {
                case null => None
                case _ =>
                  Option(
                    Correlations(
                      correlationFields = {
                        val cf =
                          me.getCorrelations.getCorrelationFields.getArray
                        CorrelationFields(n = cf.getN.toString,
                          `type` = cf.getType.value(),
                          value = cf.getValue)
                      },
                      correlationValues = {
                        val mtx =
                          me.getCorrelations.getCorrelationValues.getMatrix
                        Matrix(
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
                          },
                          matCells = mtx.getMatCells match {
                            case null => None
                            case _ =>
                              Option(mtx.getMatCells.asScala.map { c =>
                                MatCell(row = c.getRow,
                                  col = c.getCol,
                                  value = c.getValue)
                              })
                          }
                        )
                      },
                      correlationMethods =
                        me.getCorrelations.getCorrelationMethods match {
                          case null => None
                          case _ =>
                            val mtx =
                              me.getCorrelations.getCorrelationMethods.getMatrix
                            Option(
                              Matrix(
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
                                },
                                matCells = mtx.getMatCells match {
                                  case null => None
                                  case _ =>
                                    Option(mtx.getMatCells.asScala.map { c =>
                                      MatCell(row = c.getRow,
                                        col = c.getCol,
                                        value = c.getValue)
                                    })
                                }
                              ))
                        }
                    ))
              },
              predictiveModelQuality = None,
              clusteringModelQuality = None
            ))
      },
      targets = clusteringModel.getTargets match {
        case null => None
        case _ =>
          Option(clusteringModel.getTargets.asScala.map {
            t =>
              Target(
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
                  case _    => Option(t.getMin)
                },
                max = t.getMax match {
                  case null => None
                  case _    => Option(t.getMax)
                },
                rescaleConstant = t.getRescaleConstant.toDouble,
                rescaleFactor = t.getRescaleFactor.toDouble,
                targetValues = t.getTargetValues match {
                  case null => None
                  case _ =>
                    Option(t.getTargetValues.asScala.map {
                      v =>
                        TargetValue(
                          value = v.getValue match {
                            case null => None
                            case _    => Option(v.getValue)
                          },
                          displayValue = v.getDisplayValue match {
                            case null => None
                            case _    => Option(v.getDisplayValue)
                          },
                          priorProbability = v.getPriorProbability match {
                            case null => None
                            case _    => Option(v.getPriorProbability)
                          },
                          defaultValue = v.getDefaultValue match {
                            case null => None
                            case _    => Option(v.getDefaultValue)
                          }
                        )
                    })
                }
              )
          }) // this is an Iterable, not a Seq
      },
      localTransformation = clusteringModel.getLocalTransformations match {
        case null => None
        case _ =>
          Option(
            LocalTransformation(derivedFields =
              clusteringModel.getLocalTransformations.getDerivedFields match {
                case null => None
                case _ =>
                  Option(
                    clusteringModel.getLocalTransformations.getDerivedFields.asScala
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
      comparisonMeasure = ComparisonMeasure(
        kind = clusteringModel.getComparisonMeasure.getKind.value(),
        compareFunction = clusteringModel.getComparisonMeasure.getCompareFunction.value(),
        minimum = clusteringModel.getComparisonMeasure.getMinimum match {
          case null => None
          case _ => Option(clusteringModel.getComparisonMeasure.getMinimum)
        },
        maximum = clusteringModel.getComparisonMeasure.getMaximum match {
          case null => None
          case _ => Option(clusteringModel.getComparisonMeasure.getMaximum)
        },
        measure = clusteringModel.getComparisonMeasure.getMeasure match {
          case null => None
          case _ => Option(clusteringModel.getComparisonMeasure.getMeasure
            .toString
          .split("org.dmg.pmml.")
          .tail.head.split("@").head) // this should be of "Measure" type and RegEx
        }
      ),
      clusteringFields = clusteringModel.getClusteringFields.asScala.map {
        f => ClusteringField(
          field = f.getField.getValue,
          isCenterField = f.getCenterField.value(),
          fieldWeight = f.getFieldWeight,
          similarityScale = f.getSimilarityScale match {
            case null => None
            case _ => Option(f.getSimilarityScale)
          },
          compareFunction = f.getCompareFunction match {
            case null => None
            case _ => Option(f.getCompareFunction.value())
          })
      },
      missingValueWeights = clusteringModel.getMissingValueWeights match {
        case null => None
        case _ => Option(MissingValueWeights(
          n = clusteringModel.getMissingValueWeights.getArray.getN.toInt,
          `type` = clusteringModel.getMissingValueWeights.getArray.getType.value(),
          value = clusteringModel.getMissingValueWeights.getArray.getValue
        ))
      },
      clusters = clusteringModel.getClusters.asScala.map {
        c => Cluster(
          id = c.getId match {
            case null => None
            case _ => Option(c.getId)
          },
          name = c.getName match {
            case null => None
            case _ => Option(c.getName)
          },
          size = c.getSize match {
            case null => None
            case _ => Option(c.getSize)
          },
          covariances = c.getCovariances match {
            case null => None
            case _ =>
              val mtx = c.getCovariances.getMatrix
              Option(Matrix(
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
                },
                matCells = mtx.getMatCells match {
                  case null => None
                  case _ =>
                    Option(mtx.getMatCells.asScala.map { c =>
                      MatCell(row = c.getRow,
                        col = c.getCol,
                        value = c.getValue)
                    })
                }
              ))
          },
          kohonenMap = c.getKohonenMap match {
            case null => None
            case _ => Option(KohonenMap(
              coord1 = Option(c.getKohonenMap.getCoord1),
              coord2 = Option(c.getKohonenMap.getCoord2),
              coord3 = Option(c.getKohonenMap.getCoord3)
            ))
          })
      },
      modelVerification = clusteringModel.getModelVerification match {
        case null => None
        case _ =>
          val v = clusteringModel.getModelVerification
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
