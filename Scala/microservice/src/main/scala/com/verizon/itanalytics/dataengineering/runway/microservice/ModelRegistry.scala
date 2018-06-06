package com.verizon.itanalytics.dataengineering.runway.microservice

/*
 * Project: Runway
 * Alvaro Muir, Verizon IT Analytics: Data Engineering
 * 05 29, 2018
 */

import JsonProtocol._

import akka.actor.{Actor, ActorLogging, Props}

final case class Models(models: Seq[Model])

object ModelRegistry {
  final case object ReadModels
  final case class CreateModel(model: Model)
  final case class ReadModel(name: String)
  final case class UpdateModel(name: String, model: Model)
  final case class DeleteModel(name: String)
  final case class ModelActionPerformed(description: String)

  def props: Props = Props[ModelRegistry]
}

class ModelRegistry extends Actor with ActorLogging {
  import ModelRegistry._

  var models = Set.empty[Model]

  def receive: Receive = {
    case ReadModels =>
      sender() ! Models(models.toSeq)

    case ReadModel(name) =>
      sender() ! models.find(_.name == name)

    case DeleteModel(name) =>
      models.find(_.name == name) foreach { model =>
        models -= model
      }
      sender() ! ModelActionPerformed(s"Model $name deleted.")
  }
}
