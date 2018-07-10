import Dependencies._

name := "akkarest"

version := "0.1"

scalaVersion := "2.12.6"

lazy val akkarest = (project in file("."))
  .settings(
    libraryDependencies ++= akkaDependencies
  )
