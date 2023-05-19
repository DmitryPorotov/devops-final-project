ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.2"

lazy val root = (project in file("."))
  .settings(
    name := "TableGames"
  )

resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.barkhorn" % "ScalaMock" % "5.2.0"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.11"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test"

libraryDependencies += "com.lihaoyi" %% "upickle" % "1.6.0"

libraryDependencies += "org.zeromq" % "jzmq" % "3.1.0"