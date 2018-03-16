package com.verizon.itanalytics.dataengineering.runway.microservice

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}


final case class Project(name:        String,
                         models:      Option[Seq[Model]]  = None,
                         description: Option[String]  = Some(""),
                         createdOn:   Option[String]  = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date)),
                         lastUpdated: Option[String]  = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))
                        )

final case class Projects(projects: Seq[Project])

object ProjectRegistryActor {
  final case object GetProjects
  final case class CreateProject(project: Project)
  final case class GetProject(name: String)
  final case class DeleteProject(name: String)
  final case class ProjectActionPerformed(description: String)

  def props: Props = Props[ProjectRegistryActor]
}

class ProjectRegistryActor extends Actor with ActorLogging {

  import ProjectRegistryActor._

  // mock project
  var projects: Set[Project] = Seq(Project(
    "iris-test",
    Some(Seq(Model("model-1", "/some/path", Some("randomForest"), Some("iris-test"), Some("this model classifies into 3 classes a type of iris plant based on 4 physical measurement features")))),
    Some("test model description")
  )).toSet


  def receive: Receive = {
    case GetProjects =>
      sender() ! Projects(projects.toSeq)

    case CreateProject(project) =>
      val timeStamp: Option[String] = Some(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").format(new Date))

      val newProject = Project(project.name,
        project.models match {
          case None => Some(Seq.empty[Model])
          case _ => project.models
        },
        project.description match {
          case None => Some("")
          case _ => project.description
        },
        project.createdOn match {
          case None => timeStamp
          case _ => project.createdOn
        },
        project.lastUpdated match {
          case None => timeStamp
          case _ => project.lastUpdated
        }
      )
      projects += newProject
      sender() ! ProjectActionPerformed(s"Project ${newProject.name} created.")

    case GetProject(name: String) =>
      sender() ! projects.find(_.name == name)

    case DeleteProject(name) =>
      projects.find(_.name == name) foreach { project => projects -= project }
      sender() ! ProjectActionPerformed(s"Project $name deleted.")
  }
}