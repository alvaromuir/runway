package com.verizon.itanalytics.dataengineering.runway.microservice

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}


class ModelRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with ModelRoutes {
  override val modelRegistryActor: ActorRef = system.actorOf(ModelRegistryActor.props, "modelRegistry")

  lazy val routes: Route = modelRoutes

  val id = "some-model-id"
  val path =  ""
  val algorithm = Some("someAlgo")
  val project=  Some("someProject")

  "ModelRoutes" should {
    "return no models if no present (GET /models)" in {
      val request = HttpRequest(uri = "/models")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"models":[]}""")
      }
    }

    "be able to add models (POST /models)" in {
      val model = Model(id, path, algorithm, project)
      val modelEntity = Marshal(model).to[MessageEntity].futureValue
      val request = Post("/models").withEntity(modelEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===(s"""{"description":"Model $id created."}""")
      }
    }

    "be able to remove models (DELETE /models)" in {
      val request = Delete(uri = s"/models/$id")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===(s"""{"description":"Model $id deleted."}""")
      }
    }
  }
}
