package com.verizon.itanalytics.dataengineering.runway.microservice

import java.text.SimpleDateFormat

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class ProjectRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with ProjectRoutes {
  override val projectRegistryActor: ActorRef = system.actorOf(ProjectRegistryActor.props, "ProjectRegistry")

  lazy val routes: Route = projectRoutes

  val projectName = "some-project-name"
  val projectDescription = "some project description"
  lazy val projectTimeStamp: String = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").toString

  val modelId = "some-model-id"
  val modelPath =  ""
  val modelAlgorithm = Some("some-model-algorithm")
  val modelDescription = Some("some model description")
  lazy val modelTimeStamp: String = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z").toString
  val models = Seq(Model(modelId, modelPath, modelAlgorithm, Some(projectName), modelDescription))

  "ProjectRoutes" should {
      val request = HttpRequest(uri = "/projects")
      "return no Projects if no present (GET /projects)" in {
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        // todo: revisit this as in dev we start with a sample project
        entityAs[String] should ===(
          """{"projects":[]}"""
//          s"""{"projects":[{"name":"$projectName","createdOn":"$projectTimeStamp",
//             |"$projectTimeStamp","description":"$projectDescription","models":[{"createdOn":$modelTimeStamp,
//             |"lastUpdated":$modelTimeStamp,"project":"iris-test","description":$modelDescription,"id":$modelName,
//             |"algorithm":$modelAlgorithm}]}]}""".stripMargin.filter(_ >= ' ')
         )
      }
    }
    "be able to add Projects (POST /projects)" in {

      val project = Project(projectName, Some(models), Some(projectDescription))
      val projectEntity = Marshal(project).to[MessageEntity].futureValue
      val request = Post("/projects").withEntity(projectEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===(s"""{"description":"Project $projectName created."}""")
      }
    }

    "be able to remove Projects (DELETE /projects)" in {
      val request = Delete(uri = s"/projects/$projectName")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===(s"""{"description":"Project $projectName deleted."}""")
      }
    }
  }
}
