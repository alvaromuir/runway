package com.verizon.itanalytics.dataengineering.runway.microservice

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}


final case class Model(id:          String,
                       path:        String,
                       algorithm:   Option[String]  = Some(""),
                       project:     Option[String]  = Some(""),
                       description: Option[String]  = Some(""),
                       createdOn:   Option[String]  = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date)),
                       lastUpdated: Option[String]  = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))
                      )

final case class Models(models: Seq[Model])

object ModelRegistryActor {
  final case object GetModels
  final case class GetModel(id: String)
  final case class CreateModel(model: Model)
  final case class DeleteModel(id: String)
  final case class ModelActionPerformed(description: String)

  def props: Props = Props[ModelRegistryActor]
}

class ModelRegistryActor extends Actor with ActorLogging {
  import ModelRegistryActor._

  // mock model
  var models = Set.empty[Model]
//  var models: Set[Model] = Seq(
//    Model("model-1", "randomForest", "iris-test",
//      Some("this model classifies into 3 classes a type of iris plant based on 4 physical measurement features"))
//  ).toSet


  def receive: Receive = {
    case GetModels =>
      sender() ! Models(models.toSeq)

    case CreateModel(model) =>

      val timeStamp: Option[String] = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))

      val newModel = Model(model.id,
        model.path,
        model.algorithm match {
          case None => Some("")
          case _ => model.algorithm
        },
        model.project match {
          case None => Some("")
          case _ => model.project
        },
        model.description match {
          case None => Some("")
          case _ => model.description
        },
        model.createdOn match {
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
      println(id)
      models.find(_.id == id) foreach { model => models -= model }
      sender() ! ModelActionPerformed(s"Model $id deleted.")
  }
}