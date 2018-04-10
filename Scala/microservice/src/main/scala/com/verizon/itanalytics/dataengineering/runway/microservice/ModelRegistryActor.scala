package com.verizon.itanalytics.dataengineering.runway.microservice

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import org.jpmml.evaluator.Evaluator

import collection.JavaConverters._
import spray.json._
import com.verizon.itanalytics.dataengineering.runway.evaluator.Manager.getArguments


final case class InputField(name: String, dataType: String, opType: String)
final case class Model(
    id: String,
    project: Option[String] = Some(""),
    description: Option[String] = Some(""),
    path: String,
    algorithm: Option[String] = Some(""),
    inputFields: Option[List[InputField]] = None,
    createdOn: Option[String] = Some(
      new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date)),
    lastUpdated: Option[String] = Some(
      new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date)))

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
      val timeStamp: Option[String] = Some(
        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))

      val newModel = Model(
        id = model.id,
        project = model.project match {
          case None => Some("")
          case _    => model.project
        },
        description = model.description match {
          case None => Some("")
          case _    => model.description
        },
        path = model.path,
        algorithm = model.algorithm match {
          case None => Some("")
          case _    => model.algorithm
        },
        inputFields = model.inputFields match {
          case None => None
          case _    => model.inputFields
        },
        createdOn = model.createdOn match {
          case None => timeStamp
          case _    => model.createdOn
        },
        model.lastUpdated match {
          case None => timeStamp
          case _    => model.lastUpdated
        }
      )
      models += newModel
      sender() ! ModelActionPerformed(s"Model ${model.id} created.")

    case GetModel(id) =>
      sender() ! models.find(_.id == id)

    case DeleteModel(id) =>
      models.find(_.id == id) foreach { model =>
        models -= model
      }
      sender() ! ModelActionPerformed(s"Model $id deleted.")

    case GetEstimate(observation: String, evaluator: Evaluator) =>
      // todo: move this somwhere else
      implicit object MapJsonFormat extends JsonFormat[Map[String, Any]] {
        def write(m: Map[String, Any]): JsValue = {
          JsObject(m.mapValues {
            case v: String => JsString(v)
            case v: Int => JsNumber(v)
            case v: Map[_, _] => write(v.asInstanceOf[Map[String, Any]])
            case v: Any => JsString(v.toString)
          })
        }

        def read(value: JsValue) = ???
      }

        if (observation.split(",").exists(_.isEmpty)) {
        val e = "bad data, check inputs"
        sender() ! ModelActionPerformed(s"""{"results":"error: $e"}""")
      } else {
        try {
          val estimate = Option(
            evaluator
              .evaluate(getArguments(observation,
                evaluator.getInputFields, evaluator)
                .asJava)
              .asScala
              .toMap
            )


          val results = estimate
            .get
            .map {
            case (k, v: java.lang.Double  ) => k.toString -> v.toDouble
            case (k, v: java.lang.Integer ) => k.toString -> v.toInt
            case (k, v: java.lang.String  ) => k.toString -> v.toString
            case (k, v: java.lang.Boolean ) => k.toString -> v.booleanValue()
              //todo: not pretty, fix this
            case (k, v)      =>  k.toString -> Map(v.getClass.getSimpleName ->
              v.toString.split('{').tail.map (_.split('}').head).head.split('=').grouped(2)
                .map { case Array(k, v) => k -> v }
                .toMap
            )
          }.toJson

          sender() ! ModelActionPerformed(results.toString())

        } catch {
          case e: Exception =>
            sender() ! ModelActionPerformed(s"""{"results":"error: $e"}""")
        }
      }
  }
}
