
lazy val root = (project in file("."))
  .settings(
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.3.3",
    name := "TableGames",
//    Compile / packageBin / mainClass := Some("fwc.FunWithChairs"),
    libraryDependencies ++=Seq(
      "com.github.barkhorn" % "ScalaMock" % "5.2.0",
      "org.scalactic" %% "scalactic" % "3.2.18",
      "org.scalatest" %% "scalatest" % "3.2.18" % "test",
      "com.lihaoyi" %% "upickle" % "3.3.0",
      "redis.clients" % "jedis" % "5.1.2"
    ),
    Compile / packageBin / packageOptions += {
      Package.ManifestAttributes(java.util.jar.Attributes.Name.CLASS_PATH -> (
          "lib/geny_3-1.1.0.jar " +
          "lib/jedis-5.1.2.jar" +
          "lib/scala-library-2.13.12.jar " +
          "lib/scala-reflect-2.11.12.jar " +
          "lib/scala3-library_3-3.1.2.jar " +
//          "lib/scalajs-library_2.11-1.8.0.jar " +
          "lib/slf4j-api-1.7.36.jar " +
          "lib/ujson_3-3.3.0.jar " +
          "lib/upack_3-3.3.0.jar " +
          "lib/upickle_3-3.3.0.jar " +
          "lib/upickle-core_3-3.3.0.jar " +
          "lib/upickle-implicits_3-3.3.0.jar"
        ))
    }
  )

exportJars := true

resolvers += "jitpack" at "https://jitpack.io"
