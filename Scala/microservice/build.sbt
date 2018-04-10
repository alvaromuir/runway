name := "Project: Runway - MicroService"
version := "0.0.4"
organization    := "com.verizon.itanalytics.dataengineering.runway"
scalaVersion    := "2.11.8"
assemblyJarName in assembly := s"runway-microservice-${version.value}.jar"

libraryDependencies ++= {
  lazy val akkaHttpVersion = "10.1.0"
  lazy val akkaVersion = "2.5.11"
  lazy val alpakkaVersion = "0.17"
  lazy val jacksonVersion = "2.9.4"
  lazy val scalaTestVersion = "3.0.1"
  Seq(
    "com.typesafe.akka"           %% "akka-http"                % akkaHttpVersion,
    "com.typesafe.akka"           %% "akka-http-spray-json"     % akkaHttpVersion,
    "com.typesafe.akka"           %% "akka-http-xml"            % akkaHttpVersion,
    "com.typesafe.akka"           %% "akka-stream"              % akkaVersion,
    "com.typesafe.akka"           %% "akka-http-spray-json"     % akkaVersion,
    "com.typesafe.akka"           %% "akka-slf4j"               % akkaVersion,

    "com.lightbend.akka"          %% "akka-stream-alpakka-csv"  % alpakkaVersion,

    "ch.qos.logback"              % "logback-classic"           % "1.1.7",
    "de.heikoseeberger"           %% "accessus"                 % "0.1.0",


    "com.typesafe.akka"           %% "akka-http-testkit"        % akkaHttpVersion   % Test,
    "com.typesafe.akka"           %% "akka-testkit"             % akkaVersion       % Test,
    "com.typesafe.akka"           %% "akka-stream-testkit"      % akkaVersion       % Test,
    "org.scalatest"               %% "scalatest"                % scalaTestVersion  % Test
  )
}