import sbt._

object Dependencies {
  val akkaHttpVersion = "10.1.3"
  val akkaVersion     = "2.5.14"

  lazy val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-http"        % "10.1.3",
    "com.typesafe.akka" %% "akka-stream"      % akkaVersion
  )
}
