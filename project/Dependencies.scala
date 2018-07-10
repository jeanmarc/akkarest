import sbt._

object Dependencies {
  val akkaVersion = "2.5.13"
  lazy val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"      % akkaVersion
  )
}
