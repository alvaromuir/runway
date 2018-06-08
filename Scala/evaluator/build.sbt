name          := "Project: Runway - Evaluator"
organization  := "com.verizon.itanalytics.dataengineering.runway"
version       := "1.0rc-SNAPSHOT"
assemblyJarName in assembly := s"project_runway-evaluator-${version.value}.jar"

libraryDependencies ++= {
  lazy val jpmmlVersion = "1.4.1"
  lazy val scalaTestVersion = "3.0.5"
  lazy val json4sVersion = "3.6.0-M4"

  Seq(
    "org.jpmml"                  % "pmml-evaluator"           % jpmmlVersion,
    "org.jpmml"                  % "pmml-evaluator-extension" % jpmmlVersion,

    "org.json4s"                %% "json4s-jackson"           % json4sVersion,

    "org.scalactic"             %% "scalactic"                % scalaTestVersion,
    "org.scalatest"             %% "scalatest"                % scalaTestVersion    % Test,
    "org.slf4j"                  % "slf4j-api"                % "1.7.25",
    "junit"                      % "junit"                    % "4.12"              % Test,
    "com.novocode"               % "junit-interface"          % "0.11"              % Test
  )
}

mainClass in assembly := some("com.verizon.itanalytics.dataengineering.runway.microservice.Microservice")


val meta = """META.INF(.)*""".r

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}