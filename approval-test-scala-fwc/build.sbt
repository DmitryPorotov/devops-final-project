import sbt.Keys.libraryDependencies

import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
      name := "approval-test-scala-fwc",
      libraryDependencies ++=Seq(
        "org.junit.jupiter" % "junit-jupiter-api" % "5.10.2" % "test",
        "com.lihaoyi" %% "upickle" % "3.3.1",
        "com.approvaltests" % "approvaltests" % "24.2.0",
        "org.scala-lang" %% "toolkit" % "0.4.0",
        "jakarta.websocket" % "jakarta.websocket-client-api" % "2.2.0" % "provided",
        "org.java-websocket" % "Java-WebSocket" % "1.5.6",
    )
  )
