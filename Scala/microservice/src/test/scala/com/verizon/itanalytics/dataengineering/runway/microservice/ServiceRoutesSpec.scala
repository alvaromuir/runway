package com.verizon.itanalytics.dataengineering.runway.microservice

import JsonProtocol._
import Tables._
import java.io.File
import java.time.LocalDateTime

import akka.http.scaladsl.model.{StatusCodes, _}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import slick.jdbc.H2Profile.api._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.concurrent.duration._
import scala.io.Source
import scala.util.{Failure, Success}



class ServiceRoutesSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with ScalaFutures
    with ServiceRoutes
    with BeforeAndAfterEach {


  private val config: Config = ConfigFactory.load()
  private val apiVersion: String = config.getString("http.apiVersion")
  private val dataUploadPath: String = config.getString("http.dataUploadPath")

  val pathPrefix = s"/api/$apiVersion"

  lazy val routes: Route = serviceRoutes

  val sourceDir = "microservice/src/test/resources"
  val fileName = "iris_rf.pmml"
  val fileName2 = "IrisGeneralRegression.xml"
  val testJson = "iris_test.json"
  val testData = "iris_test.csv"
  val testData2 = "iris_test_headless.csv"
  val uploadDir = "/tmp"
  val name = s"${this.getClass.getSimpleName} Model Name"
  val project = Some("Some Project Name")
  val description = Some("Some model description goes here....")
  val algorithm = Some("testing algo")
  val author = Some("someUserId")
  val filePath = s"$dataUploadPath/$fileName"
  val filePath2 = s"$dataUploadPath/$fileName2"


  val heartbeatTestRoute: Route =
    get {
      pathSingleSlash {
        complete {
          "well, hello"
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
//      System.exit(1)
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

  override def beforeEach() {
    // database initialization
    val model = Model(
      name = Slugify(name),
      project = project,
      description = description,
      algorithm = algorithm,
      author = author,
      filePath = filePath
    )

    db.run(models += model)

  }


  override def afterEach() {
    // database truncation
    db.run(models.filter(_.name === name).delete)
  }

  "Model routes" should {
    "respond to a test endpoint with the appropriate information" in {

      Get(s"$pathPrefix/test") ~> serviceRoutes ~> check {
        val pattern(testTimeStamp) = LocalDateTime.now().toString
        val jsonResp = parse(responseAs[String]).extract[JsonResponse]
        assert(jsonResp.status == 200)
        assert(jsonResp.timeStamp.get.startsWith(testTimeStamp.substring(0, 15)))
        assert(jsonResp.msg.contains("This is just a test. Or is it \uD83D\uDE09"))
        assert(jsonResp.pMML.isEmpty)
        assert(jsonResp.results.isEmpty)
        assert(jsonResp.estimate.isEmpty)
      }
    }

    "return an empty list if no models present (GET /models)" in {
      db.run(models.filter(_.name === name).delete).onComplete {
        case Success(_) =>  val request = HttpRequest(uri = s"$pathPrefix/models")
          request ~> routes ~> check {
            status should ===(StatusCodes.OK)
            contentType should ===(ContentTypes.`application/json`)
            assert(parse(responseAs[String]).extract[JsonResponse].results.get.isEmpty)
          }
        case Failure(e) =>
          val errMsg = s"ERROR querying data: $e"
          log.error(errMsg)
      }
    }

    "return a list of if models present (GET /models)" in {
      val request = HttpRequest(uri = s"$pathPrefix/models")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        assert(parse(responseAs[String]).extract[JsonResponse].results.get.nonEmpty)
      }
    }

    "add models (POST /models)" in {
      implicit val timeout = RouteTestTimeout(3.seconds dilated)

      val formName = Multipart.FormData.BodyPart.Strict("name", name)
      val formProj = Multipart.FormData.BodyPart.Strict("project", project.get)
      val formDesc = Multipart.FormData.BodyPart.Strict("description", description.get)
      val formAuth = Multipart.FormData.BodyPart.Strict("author", author.get)
      val pmmlFile = new File(s"$sourceDir/$fileName").toPath
      val fileData = Multipart.FormData.BodyPart.fromPath("file", ContentTypes.`text/plain(UTF-8)`, pmmlFile)
      val formData = Multipart.FormData(formName, formProj, formDesc, formAuth, fileData)

      val request = Post(s"$pathPrefix/models", formData)

      val expectedResp = s"model: $name record created as '${Slugify(name)}'"
      request ~> routes ~> check {
        status should ===(StatusCodes.Created)
        contentType should ===(ContentTypes.`application/json`)
        assert(parse(responseAs[String]).extract[JsonResponse].msg.contains(expectedResp))
      }
    }

    "retrieve a specific model overview (GET /models/{name})" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val request = HttpRequest(uri = s"$pathPrefix/models/${Slugify(name)}")
      request ~> routes ~> check {
        val response = parse(responseAs[String]).extract[JsonResponse]
        val results = response.results.get.head

        contentType should ===(ContentTypes.`application/json`)
        status should ===(StatusCodes.OK)
        response.msg should ===(StatusCodes.OK.defaultMessage)
        assert(response.results.nonEmpty)
        results.name should ===(Slugify(name))
        results.project should ===(project)
        results.description should ===(description)
        results.algorithm should ===(algorithm) // read from file
        results.author should ===(author)
        results.filePath should ===(filePath)

      }

    }

    "get details on a specific model (GET /models/{name}/details)" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val request = HttpRequest(uri = s"$pathPrefix/models/${Slugify(name)}/details")
      request ~> routes ~> check {
        val response = parse(responseAs[String]).values.asInstanceOf[Map[String, Any]]

        contentType should ===(ContentTypes.`application/json`)
        status should ===(StatusCodes.OK)
        response("msg").toString === StatusCodes.OK.defaultMessage
        assert(response.get("pMML").nonEmpty)
      }

    }

    "evaluate a single observation (POST /models/{name})" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      var testObservation =
        Source.fromFile(s"$sourceDir/$testJson").getLines.drop(1).next

      if (testObservation.endsWith(","))
        testObservation = testObservation.dropRight(1)

      val request = Post(s"$pathPrefix/models/${Slugify(name)}",
        HttpEntity(MediaTypes.`application/json`,
          testObservation))

      request ~> routes ~> check {
        val response = parse(responseAs[String]).values.asInstanceOf[Map[String, Any]]
        val results = response("estimate").asInstanceOf[Map[String, Any]]
        assert(results.contains("Predicted_Species"))
        assert(results.contains("Probability_setosa"))
        assert(results.contains("Probability_versicolor"))
        assert(results.contains("Probability_virginica"))
      }

    }

    "evaluate a batch of observations (POST /models/{name}/batch)" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val csvFile = new File(s"$sourceDir/$testData").toPath
      val fileData = Multipart.FormData.BodyPart.fromPath("csv", ContentTypes.`text/plain(UTF-8)`, csvFile)
      val formData = Multipart.FormData(fileData)

      val request = Post(s"$pathPrefix/models/${Slugify(name)}/batch", formData)

      request ~> routes ~> check {
        val lines = responseAs[String].split("\n")
        assert(lines.length equals 150)
        assert(lines.head.contains("Probability_setosa=1.0"))
        assert(lines.last.contains("Probability_virginica=0.89"))

      }
    }

    "evaluate a batch of observations (POST /models/{name}/batch) with user-inputted fields" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val csvFile = new File(s"$sourceDir/$testData2").toPath
      val fileData = Multipart.FormData.BodyPart.fromPath("csv", ContentTypes.`text/plain(UTF-8)`, csvFile)
      val fields = Multipart.FormData.BodyPart.Strict("fields","Sepal.Length,Sepal.Width,Petal.Length,Petal.Width")
      val formData = Multipart.FormData(fileData, fields)

      val request = Post(s"$pathPrefix/models/${Slugify(name)}/batch", formData)

      request ~> routes ~> check {
        val lines = responseAs[String].split("\n")
        assert(lines.length equals 150)
        assert(lines.head.contains("Probability_setosa=1.0"))
        assert(lines.last.contains("Probability_virginica=0.89"))

      }
    }

    "update a specific model (PUT /models/{name})" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val updatedProjectName = "new project name"
      val updatedDescription = "some updated description"

      val formProj = Multipart.FormData.BodyPart.Strict("project", updatedProjectName)
      val formDesc = Multipart.FormData.BodyPart.Strict("description", updatedDescription)
      val pmmlFile = new File(s"$sourceDir/$fileName2").toPath
      val fileData = Multipart.FormData.BodyPart.fromPath("file", ContentTypes.`text/plain(UTF-8)`, pmmlFile)
      val formData = Multipart.FormData(formProj, formDesc, fileData)

      val request = Put(s"$pathPrefix/models/${Slugify(name)}", formData)

      val expectedResp = s"model: ${Slugify(name)} record updated"

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        assert(parse(responseAs[String]).extract[JsonResponse].msg.contains(expectedResp))

      }
    }

    "delete a specific model (DELETE /models/{name})" in {
      implicit val timeout: RouteTestTimeout = RouteTestTimeout(3.seconds dilated)

      val containsModel = for {
        m <- models
        if m.name === name
      } yield m

      val request = Delete(s"$pathPrefix/models/${Slugify(name)}")

      val expectedResp = s"model: ${Slugify(name)} record deleted"
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        assert(parse(responseAs[String]).extract[JsonResponse].msg.contains(expectedResp))
        val q = db.run(containsModel.exists.result)
        q.onComplete {
          case Success(row) => assert(row === false)
          case Failure(e) =>
            val errMsg = s"ERROR querying data: $e"
            log.error(errMsg)
        }

      }
    }

  }
}
