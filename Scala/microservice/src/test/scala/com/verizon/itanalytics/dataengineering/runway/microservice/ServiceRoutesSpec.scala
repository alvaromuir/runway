package com.verizon.itanalytics.dataengineering.runway.microservice

import JsonProtocol._
import Tables._
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{StatusCodes, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import slick.jdbc.H2Profile.api._
import spray.json._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ServiceRoutesSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with SprayJsonSupport
    with DefaultJsonProtocol
    with ScalaFutures
    with ServiceRoutes {


  private val config: Config = ConfigFactory.load()
  private val apiVersion: String = config.getString("http.apiVersion")
  private val dataUploadPath: String = config.getString("http.dataUploadPath")
//  private val srcDataFileName: String = config.getString("db.srcDataFileName")

  val pathPrefix = s"/api/$apiVersion"

  lazy val routes: Route = serviceRoutes

  val sourceDir = "./microservice/src/test/resources"
  val fileName = "iris_rf.pmml"
  val testJson = "iris_test.json"
  val testData = "iris_test.csv"
  val uploadDir = "/tmp"
  val name = "Some Model Name"
  val project = Some("Some Project Name")
  val description = Some("Some model description goes here....")
  val algorithm = Some("testing algo")
  val author = Some("someUserId")
  val filePath = s"$dataUploadPath/$fileName"


  val modelRegistry: ActorRef = system.actorOf(ModelRegistry.props, "modelRegistry")

  val heartbeatTestRoute: Route =
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

  initTable().onComplete {
    case Success(_) => log.info("Database successfully seeded.")
    case Failure(e) =>
      log.error(
        s"ERROR: Database seeding failed with error message: $e. Now exiting")
      System.exit(1)
  }

  val pattern = """^(.*?)\..*""".r


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

  "Model routes" should {
    "respond to a test endpoint with the appropriate information" in {

      Get(s"$pathPrefix/test") ~> serviceRoutes ~> check {
        val pattern(testTimeStamp) = LocalDateTime.now().toString
        val jsonResp = responseAs[JsonResponse]
        assert(jsonResp.status == 200)
        assert(jsonResp.timeStamp.get.contains(testTimeStamp))
        assert(jsonResp.msg.contains("This is just a test. Or is it \uD83D\uDE09"))
        assert(jsonResp.results.isEmpty)

      }
    }

    "return an empty list if no models present (GET /models)" in {
      val request = HttpRequest(uri = s"$pathPrefix/models")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        assert(responseAs[JsonResponse].results.isEmpty)
      }
    }

    "add models (POST /models)" in {
      implicit val timeout = RouteTestTimeout(3.seconds dilated)

      val formId = Multipart.FormData.BodyPart.Strict("name", name)
      val formProj = Multipart.FormData.BodyPart.Strict("project", project.get)
      val formDesc =
        Multipart.FormData.BodyPart.Strict("description", description.get)
      val pmmlFile = new File(s"$sourceDir/$fileName").toPath
      val fileData = Multipart.FormData.BodyPart
        .fromPath("file", ContentTypes.`text/plain(UTF-8)`, pmmlFile)
      val formData = Multipart.FormData(formId, formProj, formDesc, fileData)

      val request = Post(s"$pathPrefix/models", formData)

      val expectedResp = s"model: $name record created as '${Slugify(name)}'."
      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        assert(responseAs[JsonResponse].msg.contains(expectedResp))
      }
    }

    "retrieve a specific model overview (GET /models/{name})" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val model = Model(
        name = Slugify(name),
        project = project,
        description = description,
        algorithm = algorithm,
        author = author,
        filePath = filePath
      )

      whenReady(db.run(models += model)) {
        _ =>
          val testDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date)
          val request = HttpRequest(uri = s"$pathPrefix/models/${Slugify(name)}")
          request ~> routes ~> check {
            val response = entityAs[JsonResponse]
            val results = response.results.get.head
            val pattern(testTimeStamp) = LocalDateTime.now().toString


            contentType should ===(ContentTypes.`application/json`)
            status should ===(StatusCodes.OK)
            response.timeStamp.get should startWith(testTimeStamp)
            response.msg should ===(StatusCodes.OK.defaultMessage)
            assert(response.results.nonEmpty)
            results.id should ===(1)
            results.name should ===(Slugify(name))
            results.project should ===(project)
            results.description should ===(description)
            results.algorithm should ===(algorithm)
            results.author should ===(author)
            results.filePath should ===(filePath)
            results.createdOn.get should startWith(testDateTime)
            results.lastUpdated.get should startWith(testDateTime)
          }
      }
    }

    "get details on a specific model (GET /models/{name}/details)" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val model = Model(
        name = Slugify(name),
        project = project,
        description = description,
        algorithm = algorithm,
        author = author,
        filePath = filePath
      )

      whenReady(db.run(models += model)) {
        _ =>
          val testDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date)
          val request = HttpRequest(uri = s"$pathPrefix/models/${Slugify(name)}/details")
          request ~> routes ~> check {
            println(responseAs[String])
//            val response = entityAs[JsonResponse]
//            val results = response.results.get.head
//            val pattern(testTimeStamp) = LocalDateTime.now().toString
//
//
//            contentType should ===(ContentTypes.`application/json`)
//            status should ===(StatusCodes.OK)
//            response.timeStamp.get should startWith(testTimeStamp)
//            response.msg should ===(StatusCodes.OK.defaultMessage)
//            assert(response.results.nonEmpty)
//            results.id should ===(1)
//            results.name should ===(Slugify(name))
//            results.project should ===(project)
//            results.description should ===(description)
//            results.algorithm should ===(algorithm)
//            results.author should ===(author)
//            results.filePath should ===(filePath)
//            results.createdOn.get should startWith(testDateTime)
//            results.lastUpdated.get should startWith(testDateTime)
          }
      }

    }

  }

//  "ModelRoutes" should {

//

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
