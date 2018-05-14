package com.verizon.itanalytics.dataengineering.runway.evaluator

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 04 24, 2018
 */

import java.io.{File, FileInputStream, FileNotFoundException}
import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Locale}

import com.verizon.itanalytics.dataengineering.runway.evaluator.models._
import javax.xml.bind.JAXBException
import com.verizon.itanalytics.dataengineering.runway.evaluator.schemas.PMMLSchema
import org.dmg.pmml.{FieldName, Model, PMML}
import org.jpmml.evaluator.{ModelEvaluator, ModelEvaluatorFactory, ReportingValueFactoryFactory, ValueFactoryFactory}
import org.jpmml.model.PMMLUtil
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.control.ControlThrowable

trait Evaluator
    extends Utils
    with PMMLSchema
    with AssociationRules
    with BayesianNetwork
    with ClusterModel
    with GaussianProcess
    with GeneralRegression
    with NeuralNetwork
    with NaiveBayes
    with Regression
    with RuleSet
    with ScoreCard
    with TextModels
    with TimeSeries
    with VectorMachine {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  /** Returns a pMML file from input stream
    *
    * @param file an input file stream
    * @throws JAXBException when there are problems parsing the pMML file
    * @throws FileNotFoundException when files are not available in file system
    * @return a valid pMML object
    */
  @throws[JAXBException]
  @throws[FileNotFoundException]
  def readPMML(file: File): PMML =
    try {
      val input = new FileInputStream(file)
      try { PMMLUtil.unmarshal(input) } catch safely {
        case ex: ControlThrowable => throw ex
      } finally if (input != null) input.close()
    } catch safely { case ex: ControlThrowable => throw ex }

  /** Returns a jpmml-evaluator Evaluator
    *
    * @param pMML a valid PMML File
    * @throws Exception for parsing errors
    * @return Evaluator
    *
    */
  @throws[Exception]
  def evaluatePmml(pMML: PMML): ModelEvaluator[_ <: Model] = {
    val valueFactoryFactory: ValueFactoryFactory =
      ReportingValueFactoryFactory.newInstance
    val modelEvaluatorFactory: ModelEvaluatorFactory =
      ModelEvaluatorFactory.newInstance

    modelEvaluatorFactory.setValueFactoryFactory(valueFactoryFactory)
    modelEvaluatorFactory.newModelEvaluator(pMML)
  }

  /**
    * Returns a PmmlSchema Model with appropriate keys
    * @param pMML a valid PMML File
    * @return PmmlSchema
    */
  def parsePmml(pMML: PMML): PMMLSchema = {
    val evaluator: ModelEvaluator[_ <: Model] = evaluatePmml(pMML)
    val header = pMML.getHeader
    val miningBuildTask = pMML.getMiningBuildTask
    val dataDictionary = pMML.getDataDictionary
    val transformationDictionary = pMML.getTransformationDictionary
    val formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                             Locale.ENGLISH).format(new Date())

    PMMLSchema(
      header = Header(
        copyright = Option(header.getCopyright),
        description = Option(header.getDescription),
        modelVersion = Option(header.getModelVersion),
        application = header.getApplication match {
          case null => None
          case _ =>
            Option(
              Application(name = header.getApplication.getName,
                          version = Option(header.getApplication.getVersion)))
        },
        annotations = Option(header.getAnnotations.asScala.map(_.toString)), //revisit, make Extensions (once we see one)
        timeStamp = Option(header.getTimestamp.formatted(formattedDate))
      ),
      miningBuildTask = miningBuildTask match {
        case null => None
        case _    => Option(pMML.getMiningBuildTask.toString)
      },
      dataDictionary = DataDictionary(
        numOfFields = dataDictionary.getNumberOfFields.toInt,
        taxonomies = dataDictionary.getTaxonomies match {
          case null => None
          case _ =>
            Option(dataDictionary.getTaxonomies.asScala.map {
              t =>
                Taxonomy(
                  name = t.getName,
                  childParents = t.getChildParents match {
                    case null => None
                    case _ =>
                      Option(
                        t.getChildParents.asScala.map(cp =>
                          ChildParent(
                            childField = cp.getChildField,
                            parentField = cp.getParentField,
                            parentLevelField = cp.getParentLevelField match {
                              case null => None
                              case _    => Option(cp.getParentLevelField)
                            },
                            isRecursive = cp.getRecursive.value(), // this is probably a boolean
                            tableLocator = cp.getTableLocator match {
                              case null => None
                              case _ => Option(TableLocator(
                                extension = cp.getTableLocator.getExtensions match {
                                  case null => None
                                  case _=>
                                    Option(cp.getTableLocator.getExtensions.asScala.map {
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
                                            case _    => Option(e.getContent.asScala.map { _.toString })
                                          }
                                        )
                                    })
                                }
                              ))
                            },
                            inlineTables = cp.getInlineTable match {
                              case null => None
                              case _ => Option(InlineTable(
                                extension = cp.getInlineTable.getExtensions match {
                                  case null => None
                                  case _ =>
                                    Option(cp.getInlineTable.getExtensions.asScala.map {
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
                                            case _    => Option(e.getContent.asScala.map { _.toString })
                                          }
                                        )
                                    })
                                },
                                row = cp.getInlineTable.getRows match {
                                  case null => None
                                  case _ => Option(cp.getInlineTable.getRows.asScala.map {
                                    r => Row(content = r.getContent.asScala.map { _.toString})
                                  })
                                }
                              ))
                            }
                        )))
                  }
                )
            })
        },
        dataFields = dataDictionary.getDataFields match {
          case null => None
          case _ =>
            Option(dataDictionary.getDataFields.asScala.map {
              f =>
                DataField(
                  name = f.getName.getValue,
                  displayName = f.getDisplayName match {
                    case null => None
                    case _    => Option(f.getDisplayName)
                  },
                  optype = f.getOpType.value(),
                  dataType = f.getDataType.value(),
                  taxonomy = f.getTaxonomy match {
                    case null => None
                    case _    => Option(f.getTaxonomy)
                  },
                  isCyclic = f.getCyclic match {
                    case null => None
                    case _    => Option(f.getCyclic.value())
                  },
                  intervals = f.getIntervals match {
                    case null => None
                    case _ =>
                      Option(f.getIntervals.asScala.map { i =>
                        Interval(
                          closure = i.getClosure.value(),
                          leftMargin = i.getLeftMargin match {
                            case null => None
                            case _    => Option(i.getLeftMargin)
                          },
                          rightMargin = i.getRightMargin match {
                            case null => None
                            case _    => Option(i.getRightMargin)
                          }
                        )
                      })
                  }
                )
            })
        }
      ),
      transformationDictionary = transformationDictionary match {
        case null => None
        case _ =>
          Option(
            TransformationDictionary(
              defineFunctions =
                transformationDictionary.getDefineFunctions match {
                  case null => None
                  case _ =>
                    Option(
                      transformationDictionary.getDefineFunctions.asScala.map {
                        fn =>
                          DefineFunction(
                            name = fn.getName,
                            optype = fn.getOpType.value(),
                            dataType = fn.getDataType match {
                              case null => None
                              case _    => Option(fn.getDataType.value)
                            },
                            key = fn.getKey match {
                              case null => None
                              case _    => Option(fn.getKey)
                            },
                            parameterFields = fn.getParameterFields match {
                              case null => None
                              case _ =>
                                Option(fn.getParameterFields.asScala.map { p =>
                                  ParameterField(
                                    name = p.getName.getValue,
                                    optype = p.getOpType.value(),
                                    dataType = p.getDataType match {
                                      case null => None
                                      case _    => Option(p.getDataType.value)
                                    }
                                  )
                                })
                            }
                          )
                      })
                },
              derivedFields = transformationDictionary.getDerivedFields match {
                case null => None
                case _ =>
                  Option(transformationDictionary.getDerivedFields.asScala.map {
                    df =>
                      DerivedField(
                        name = df.getName match {
                          case null => None
                          case _    => Option(df.getName.getValue)
                        },
                        displayName = df.getDisplayName match {
                          case null => None
                          case _    => Option(df.getDisplayName)
                        },
                        optype = df.getOpType.value(),
                        dataType = df.getDataType.value(),
                        intervals = df.getIntervals match {
                          case null => None
                          case _ =>
                            Option(df.getIntervals.asScala.map { i =>
                              Interval(
                                closure = i.getClosure.value,
                                leftMargin = i.getLeftMargin match {
                                  case null => None
                                  case _    => Option(i.getLeftMargin)
                                },
                                rightMargin = i.getRightMargin match {
                                  case null => None
                                  case _    => Option(i.getRightMargin)
                                }
                              )
                            })
                        }
                      )
                  })
              }
            ))
      },
      version = pMML.getVersion,
      associationModel = evaluator.getSummary match {
        case "Association rules" => Option(parseAssociationModel(pMML))
        case _                   => None
      },
      bayesianNetworkModel = evaluator.getSummary match {
        case "Bayesian network" => Option(parseBayesianNetworkModel(pMML))
        case _ => None
      },
      clusteringModel = evaluator.getSummary match {
        case "Clustering model" => Option(parseClusteringModel(pMML))
        case _                  => None
      },
      gaussianProcessModel = evaluator.getSummary match {
        case "Gaussian process" => Option(parseGaussianProcessModel(pMML))
        case _                  => None
      },
      generalRegressionModel = evaluator.getSummary match {
        case "General regression" => Option(parseGeneralRegressionModel(pMML))
        case _                    => None
      },
      miningModel = evaluator.getSummary match {
        case _ => None
      },
      naiveBayesModel = evaluator.getSummary match {
        case "Naive Bayes model" => Option(parseNaiveBayesModel(pMML))
        case _                   => None
      },
      nearestNeighborModel = evaluator.getSummary match {
        case _ => None
      },
      neuralNetwork = evaluator.getSummary match {
        case "Neural network" => Option(parseNeuralNetworkModel(pMML))
        case _                => None
      },
      regressionModel = evaluator.getSummary match {
        case "Regression" => Option(parseRegressionModel(pMML))
        case _            => None
      },
      ruleSetModel = evaluator.getSummary match {
        case "Ruleset model" => Option(parseRulSetModel(pMML))
        case _ => None
      },
      sequenceModel = evaluator.getSummary match {
        case _ => None
      },
      scoreCardModel = evaluator.getSummary match {
        case "Scorecard model" => Option(parseScoreCardModel(pMML))
        case _ => None
      },
      supportVectorMachineModel = evaluator.getSummary match {
        case "Support vector machine" => Option(parseSupportVectorMachineModel(pMML))
        case _ => None
      },
      textModel = evaluator.getSummary match {
        case "Text model" => Option(parseTextModel(pMML))
        case _ => None
      },
      timeSeriesModel = evaluator.getSummary match {
        case "Time series" => Option(parseTimeSeriesModel(pMML))
        case _ => None
      },
      treeModel = evaluator.getSummary match {
        case _ => None
      }
    )
  }

  /**
    * Returns hashmap of arguments used in making predictions
    * @param pmmlModel a valid PMMLSchema
    * @param features a immutable Map[Any, Any]
    * @return LinkedHashMap[FieldName, FieldValue]
    */
  def createArguments(pmmlModel: PMMLSchema,
                      features: Map[Any, Any]): util.Map[FieldName, Any] = {
    val arguments = new mutable.LinkedHashMap[FieldName, Any]


//    todo: improve this logic
    pmmlModel.associationModel match {
      case None =>
      case Some(_) =>
        features.mapValues(_.asInstanceOf[List[Any]].asJava).map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    pmmlModel.bayesianNetworkModel match {
      // not yet implemented
      case None    =>
      case Some(_) =>
    }

    pmmlModel.clusteringModel match {
      case None =>
      case Some(_) =>
        features.map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    pmmlModel.generalRegressionModel match {
      case None =>
      case Some(_) =>
        features.map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    pmmlModel.gaussianProcessModel match {
      // not yet implemented
      case None    =>
      case Some(_) =>
    }

    pmmlModel.naiveBayesModel match {
      // not yet implemented
      case None    =>
      case Some(_) =>
    }

    pmmlModel.nearestNeighborModel match {
      // not yet implemented
      case None    =>
      case Some(_) =>
    }

    pmmlModel.neuralNetwork match {
      case None =>
      case Some(_) =>
        features.map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    pmmlModel.regressionModel match {
      case None =>
      case Some(_) =>
        features.map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    pmmlModel.regressionModel match {
      case None =>
      case Some(_) =>
        features.map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    pmmlModel.supportVectorMachineModel match {
      case None =>
      case Some(_) =>
        features.map {
          case (k, v) => arguments.put(FieldName.create(k.toString), v)
        }
    }

    arguments.asJava
  }

}
