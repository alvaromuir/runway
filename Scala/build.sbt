name := "Project: Runway"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  val akkaVersion = "2.4.11.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"  % akkaVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  )
}

libraryDependencies ++= {
  val jpmmlVersion = "1.4.0"
  Seq(
    "org.jpmml" % "pmml-evaluator" % jpmmlVersion,
    "org.jpmml" % "pmml-evaluator-extension" % jpmmlVersion
  )
}
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "junit" % "junit" % "4.12" % Test
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test

libraryDependencies ++= {
  val scalaTestVersion = "3.0.5"
  Seq(
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  )
}