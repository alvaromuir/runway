package com.verizon.itanalytics.dataengineering.runway.microservice

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import com.verizon.itanalytics.dataengineering.runway.microservice.ModelRegistryActor.ModelActionPerformed
import com.verizon.itanalytics.dataengineering.runway.microservice.ProjectRegistryActor.ProjectActionPerformed

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val modelJsonFormat: RootJsonFormat[Model] = jsonFormat7(Model)
  implicit val modelsJsonFormat: RootJsonFormat[Models] = jsonFormat1(Models)
  implicit val modelActionPerformedJsonFormat: RootJsonFormat[ModelActionPerformed] = jsonFormat1(ModelActionPerformed)

  implicit val projectJsonFormat: RootJsonFormat[Project] = jsonFormat5(Project)
  implicit val projectsJsonFormat: RootJsonFormat[Projects] = jsonFormat1(Projects)
  implicit val projectActionPerformedJsonFormat: RootJsonFormat[ProjectActionPerformed] = jsonFormat1(ProjectActionPerformed)
}