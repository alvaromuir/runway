name          := "Project: Runway - MicroService"
organization  := "com.verizon.itanalytics.dataengineering.runway"
version       := "1.0rc-SNAPSHOT"
assemblyJarName in assembly := s"project_runway-microservice-${version.value}.jar"

libraryDependencies ++= {
  lazy val akkaHttpVersion = "10.1.0"
  lazy val akkaVersion = "2.5.11"
  lazy val alpakkaVersion = "0.20"
  lazy val json4sVersion = "3.6.0-M4"
  lazy val heikoseebergerVersion = "1.21.0"
  lazy val slickVersion = "3.2.3"
  lazy val scalaTestVersion = "3.0.1"



  Seq(
    "com.typesafe.akka"           %% "akka-http"                  % akkaHttpVersion,
    "com.typesafe.akka"           %% "akka-http-xml"              % akkaHttpVersion,
    "com.typesafe.akka"           %% "akka-http-spray-json"       % akkaHttpVersion,
    "com.typesafe.akka"           %% "akka-stream"                % akkaVersion,
    "com.typesafe.akka"           %% "akka-slf4j"                 % akkaVersion,

    "com.typesafe.slick"          %% "slick"                      % slickVersion,

    "com.lightbend.akka"          %% "akka-stream-alpakka-csv"    % alpakkaVersion,
    "com.lightbend.akka"          %% "akka-stream-alpakka-slick"  % alpakkaVersion,

    "com.lightbend.akka"          %% "akka-stream-alpakka-csv"    % alpakkaVersion,

    "org.json4s"                  %% "json4s-jackson"             % json4sVersion,
    "de.heikoseeberger"           %% "akka-http-Json4s"           % heikoseebergerVersion,

    "ch.qos.logback"               % "logback-classic"            % "1.1.7",
    "de.heikoseeberger"           %% "accessus"                   % "0.1.0",

    "com.h2database"               % "h2"                         % "1.4.192",

    "com.typesafe.akka"           %% "akka-http-testkit"          % akkaHttpVersion   % Test,
    "com.typesafe.akka"           %% "akka-testkit"               % akkaVersion       % Test,
    "com.typesafe.akka"           %% "akka-stream-testkit"        % akkaVersion       % Test,
    "com.typesafe.slick"          %% "slick-testkit"              % slickVersion      % Test,
    "org.scalatest"               %% "scalatest"                  % scalaTestVersion  % Test
  )
}