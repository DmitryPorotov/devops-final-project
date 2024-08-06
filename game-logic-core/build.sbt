val scala3Version = "3.3.3"


lazy val root = (project in file("."))
  .settings(
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    name := "TableGames",
    version := "0.1.0-SNAPSHOT",
  ) aggregate(scala2Project, scala3MainProject, serverModule) dependsOn(scala2Project, scala3MainProject, serverModule)

lazy val scala2Project = project
  .in(file("scala2project"))
  .settings(
    name := "scala2project",
    scalaVersion := "2.13.14",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq()
  )

lazy val scala3MainProject = project
  .in(file("scala3MainProject"))
  .settings(
    name := "scala3MainProject",
    scalaVersion := scala3Version,
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++=Seq(
      "org.scalamock" %% "scalamock" % "6.0.0" % "test",
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.18" % "test",
      "com.lihaoyi" %% "upickle" % "3.3.1",
//      "redis.clients" % "jedis" % "5.1.3",
    ),
  )

lazy val serverModule = project
  .in(file("serverModule"))
  .settings(
    name := "serverModule",
    scalaVersion := scala3Version,
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++=Seq(
      "com.lihaoyi" %% "upickle" % "3.3.1",
      "redis.clients" % "jedis" % "5.1.3",
    ),
  ) dependsOn scala3MainProject

ThisBuild / assemblyMergeStrategy  := {
  case PathList("module-info.class") => MergeStrategy.discard
  case x if x.endsWith("/module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

resolvers +=
  "DataStax-Repo" at "https://repo.datastax.com/public-repos/"

val jarName = "worker.jar"
assembly/assemblyJarName := jarName

lazy val gen = (project in file("."))
  .settings(
    name := "code-generation",
    scalaVersion := scala3Version,
    version := "0.1.0-SNAPSHOT"
  ) dependsOn scala3MainProject