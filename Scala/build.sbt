name          := "Project: Runway"
organization  := "com.verizon.itanalytics.dataengineering.runway"
version       := "1.0rc-SNAPSHOT"
scalaVersion  := "2.11.12"

val shared = Seq(
  scalacOptions ++= Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  ),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),
  organization := "com.verizon.itanalytics.dataengineering",
  version := "0.1",
  scalaVersion := "2.11.12"
)

lazy val Runway = (project in file("."))
  .aggregate(Evaluator)
  .settings(
    shared,
    name := "Project: Runway"
  )
  .aggregate(
    Evaluator,
    MicroService
  )

lazy val Evaluator = (project in file("evaluator"))
  .settings(
    shared,
    name := "runway-evaluator",
    description := "Project: Runway -- evaluator",
    libraryDependencies ++= scalastic.value,
    libraryDependencies ++= scalatest.value
  )

lazy val MicroService = (project in file("microservice"))
  .settings(
    shared,
    name := "runway-microservice",
    description := "Project: Runway -- microservice",
    libraryDependencies ++= scalastic.value,
    libraryDependencies ++= scalatest.value
  )
  .dependsOn(
    Evaluator
  )

val scalaTestVersion = "3.0.5"
def scalastic = Def.setting {
  scalaBinaryVersion.value match {
    case "2.10" => Nil
    case _      => ("org.scalactic" %% "scalactic" % scalaTestVersion) :: Nil
  }
}

def scalatest = Def.setting {
  scalaBinaryVersion.value match {
    case "2.10" => Nil
    case _      => ("org.scalatest" %% "scalatest" % scalaTestVersion % Test) :: Nil
  }
}

assemblyJarName in assembly := s"runway-${version.value}.jar"