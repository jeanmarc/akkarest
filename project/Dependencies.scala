import sbt._

object Dependencies {
  lazy val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % "2.5.13"
  )
}
