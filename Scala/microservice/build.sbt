name := "Project: Runway - MicroService"
version := "0.0.1"
organization    := "com.verizon.itanalytics.dataengineering.runway"
scalaVersion    := "2.11.8"

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
    "com.lightbend.akka"          %% "akka-stream-alpakka-csv"  % alpakkaVersion,
    "com.fasterxml.jackson.core"    % "jackson-databind"        % jacksonVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala"    % jacksonVersion,


    "com.typesafe.akka"           %% "akka-http-testkit"        % akkaHttpVersion   % Test,
    "com.typesafe.akka"           %% "akka-testkit"             % akkaVersion       % Test,
    "com.typesafe.akka"           %% "akka-stream-testkit"      % akkaVersion       % Test,
    "org.scalatest"               %% "scalatest"                % scalaTestVersion  % Test
  )
}