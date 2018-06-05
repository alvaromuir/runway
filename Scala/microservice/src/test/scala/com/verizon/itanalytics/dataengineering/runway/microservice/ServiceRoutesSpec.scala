package com.verizon.itanalytics.dataengineering.runway.microservice

import java.io.File

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import spray.json._

import scala.concurrent.duration._
import scala.io.Source



class ModelRoutesSpec extends WordSpec with Matchers with ScalatestRouteTest with ServiceRoutes  {

  lazy val routes: Route = serviceRoutes

  val id = "some-model-id"
  val sourceDir = "./microservice/src/test/resources"
  val fileName = "iris_rf.pmml"
  val testJson = "iris_test.json"
  val uploadDir = "/tmp"
  val algorithm = Some("randomForest") // read from pMML file, hardcoded here..
  val project = Some("someProject")
  val descrip = Some("Description")


  val heartbeatTestRoute =
    get {
      pathSingleSlash {
        complete {
          "well, hello 😉"
        }
      } ~
        path("hello") {
          complete("World!")
        }
    }




  "The microservice" should {

    "return a greeting for GET requests to the root path" in {
      // tests:
      Get() ~> heartbeatTestRoute ~> check {
        responseAs[String] shouldEqual "well, hello 😉"
      }
    }

    "return a 'World!' response for GET requests to /ping" in {
      Get("/hello") ~> heartbeatTestRoute ~> check {
        responseAs[String] shouldEqual "World!"
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/foo") ~> heartbeatTestRoute ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> Route.seal(heartbeatTestRoute) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }
  }

//  "ModelRoutes" should {
//    "return no models if no present (GET /models)" in {
//      val request = HttpRequest(uri = "/models")
//
//      request ~> routes ~> check {
//        status should ===(StatusCodes.OK)
//        contentType should ===(ContentTypes.`application/json`)
//        entityAs[String] should ===("""{"models":[]}""")
//      }
//    }
//
//    "be able to add models (POST /models)" in {
////      val model = Model(id, path, algorithm, project)
////      val modelEntity = Marshal(model).to[MessageEntity].futureValue
//
//      implicit val timeout = RouteTestTimeout(3.seconds dilated)
//      val formId = Multipart.FormData.BodyPart.Strict("id", id)
//      val formProj = Multipart.FormData.BodyPart.Strict("project", project.get)
//
//      val formDesc =
//        Multipart.FormData.BodyPart.Strict("description", descrip.get)
//
//      val pmmlFile = new File(s"$sourceDir/$fileName").toPath
//
//      val fileData = Multipart.FormData.BodyPart
//        .fromPath("file", ContentTypes.`text/plain(UTF-8)`, pmmlFile)
//
//      val formData = Multipart.FormData(formId, formProj, formDesc, fileData)
//      val request = Post("/models", formData)
//
//      request ~> routes ~> check {
//        status should ===(StatusCodes.Created)
//        contentType should ===(ContentTypes.`application/json`)
//        entityAs[String] should ===(s"""{"description":"Model $id created."}""")
//      }
//    }
//
//    "be able retrieve a specific model (GET /models/{id})" in {
//      implicit val timeout: RouteTestTimeout =
//        RouteTestTimeout(3.seconds dilated)
//
//      val request = Get(uri = s"/models/$id")
//
//      request ~> routes ~> check {
//        status should ===(StatusCodes.OK)
//        contentType should ===(ContentTypes.`application/json`)
//        val response = entityAs[Model]
//        response.id should ===(id)
//        response.path should ===(s"$uploadDir/$fileName")
//        response.description should ===(descrip)
//        response.algorithm should ===(algorithm)
//      }
//    }
//
//    "be able to describe observation expected feature set" in {
//      implicit val timeout: RouteTestTimeout =
//        RouteTestTimeout(3.seconds dilated)
//
//      val request = Get(uri = s"/models/$id")
//      request ~> routes ~> check {
//        status should ===(StatusCodes.OK)
//        contentType should ===(ContentTypes.`application/json`)
//
//        val response = entityAs[Model]
//        val inputFields = response.inputFields.head
//
//        inputFields.size should ===(4)
//        inputFields foreach { f =>
//          f.dataType should ===("DOUBLE")
//          f.opType should ===("CONTINUOUS")
//        }
//      }
//    }
//
//    "be able to evaluate a single observation (POST /models/{id})" in {
//      implicit val timeout: RouteTestTimeout =
//        RouteTestTimeout(3.seconds dilated)
//
//      var testObservation =
//        Source.fromFile(s"$sourceDir/$testJson").getLines.drop(1).next
//
//      if (testObservation.endsWith(","))
//        testObservation = testObservation.dropRight(1)
//
//      val request =
//        Post(s"/models/$id",
//             HttpEntity(MediaTypes.`application/json`, testObservation))
//
//      request ~> routes ~> check {
//        val response = entityAs[String].stripMargin.parseJson.asJsObject
//        val results = response.getClass.getDeclaredFields
//          .map(_.getName)
//          .zip(response.productIterator.to)
//          .toMap
//          .get("fields")
//          .head
//          .asInstanceOf[Map[String, Any]]
//        assert(results.contains("Predicted_Species"))
//        assert(results.contains("Probability_setosa"))
//        assert(results.contains("Probability_versicolor"))
//        assert(results.contains("Probability_virginica"))
//      }
//    }
//
//    //todo: test for batch encoding
//
//    "be able to remove models (DELETE /models/{id})" in {
//      val request = Delete(uri = s"/models/$id")
//      request ~> routes ~> check {
//        status should ===(StatusCodes.OK)
//        contentType should ===(ContentTypes.`application/json`)
//        entityAs[String] should ===(s"""{"description":"Model $id deleted."}""")
//      }
//    }
//
//  }
}

