name          := "Project: Runway - Evaluator"
organization  := "com.verizon.itanalytics.dataengineering.runway"
version       := "0.0.1"
scalaVersion  := "2.11.8"

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