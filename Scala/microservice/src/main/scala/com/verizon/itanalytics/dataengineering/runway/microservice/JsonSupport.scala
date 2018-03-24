package com.verizon.itanalytics.dataengineering.runway.microservice

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import com.verizon.itanalytics.dataengineering.runway.microservice.ModelRegistryActor.ModelActionPerformed

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val inputFieldsJsonFormat: RootJsonFormat[InputField] = jsonFormat3(InputField)
  implicit val modelJsonFormat: RootJsonFormat[Model] = jsonFormat8(Model)
  implicit val modelsJsonFormat: RootJsonFormat[Models] = jsonFormat1(Models)
  implicit val modelActionPerformedJsonFormat: RootJsonFormat[ModelActionPerformed] = jsonFormat1(ModelActionPerformed)
}