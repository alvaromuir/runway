package com.verizon.itanalytics.dataengineering.runway.microservice


import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.Logger

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.scaladsl.Framing
import akka.util.ByteString
import com.verizon.itanalytics.dataengineering.runway.evaluator.Manager.{getArguments, getEvaluator, readPMML}
import org.jpmml.evaluator.{Evaluator, FieldValue, InvalidResultException}
import spray.json._

import scala.collection.JavaConversions._
import scala.concurrent.Future

final case class InputField(name: String, dataType: String, opType: String)
final case class Model(id:          String,
                       project:     Option[String]  = Some(""),
                       description: Option[String]  = Some(""),
                       path:        String,
                       algorithm:   Option[String]  = Some(""),
                       inputFields: Option[List[InputField]] = None,
                       createdOn:   Option[String]  = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date)),
                       lastUpdated: Option[String]  = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))
                      )

final case class Models(models: Seq[Model])

object ModelRegistryActor {
  final case object GetModels
  final case class GetModel(id: String)
  final case class CreateModel(model: Model)
  final case class DeleteModel(id: String)
  final case class GetEstimate(id: String, observation: Any)
  final case class ModelActionPerformed(description: String)

  def props: Props = Props[ModelRegistryActor]
}

class ModelRegistryActor extends Actor with ActorLogging {
  import ModelRegistryActor._

  var models = Set.empty[Model]

  def receive: Receive = {
    case GetModels =>
      sender() ! Models(models.toSeq)

    case CreateModel(model) =>

      val timeStamp: Option[String] = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))

      val newModel = Model(id = model.id,
        project = model.project match {
          case None => Some("")
          case _ => model.project
        },
        description = model.description match {
          case None => Some("")
          case _ => model.description
        },
        path = model.path,
        algorithm = model.algorithm match {
          case None => Some("")
          case _ => model.algorithm
        },
        inputFields = model.inputFields match {
          case None => None
          case _ => model.inputFields
        },
        createdOn = model.createdOn match {
          case None => timeStamp
          case _ => model.createdOn
        },
        model.lastUpdated match {
          case None => timeStamp
          case _ => model.lastUpdated
        }
      )
      models += newModel
      sender() ! ModelActionPerformed(s"Model ${model.id} created.")

    case GetModel(id) =>
      sender() ! models.find(_.id == id)

    case DeleteModel(id) =>
      models.find(_.id == id) foreach { model => models -= model }
      sender() ! ModelActionPerformed(s"Model $id deleted.")

    case GetEstimate(observation: String, evaluator: Evaluator) =>
      // todo: improve json result
      val results = try {
        evaluator.evaluate(getArguments(observation,
          evaluator.getInputFields,
          evaluator))
      }
      catch {
        case e: Exception => log.info(e.getMessage)
      }
      sender() ! ModelActionPerformed(s"$results")
  }
}