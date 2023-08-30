
lazy val root = (project in file("."))
  .settings(
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.1.2",
    name := "TableGames",
//    Compile / packageBin / mainClass := Some("fwc.FunWithChairs"),
    libraryDependencies ++=Seq(
      "com.github.barkhorn" % "ScalaMock" % "5.2.0",
      "org.scalactic" %% "scalactic" % "3.2.11",
      "org.scalatest" %% "scalatest" % "3.2.11" % "test",
      "com.lihaoyi" %% "upickle" % "1.6.0",
      "org.zeromq" % "jzmq" % "3.1.0"
    ),
    Compile / packageBin / packageOptions += {
      Package.ManifestAttributes(java.util.jar.Attributes.Name.CLASS_PATH -> (
        "lib/geny_3-0.7.1.jar " +
        "lib/ujson_3-1.6.0.jar " +
        "lib/upack_3-1.6.0.jar " +
        "lib/upickle-core_3-1.6.0.jar " +
        "lib/upickle-implicits_3-1.6.0.jar " +
        "lib/upickle_3-1.6.0.jar " +
        "lib/scala-library-2.13.8.jar " +
        "lib/scala-reflect-2.11.12.jar " +
        "lib/scala3-library_3-3.1.2.jar " +
        "lib/jzmq-3.1.0.jar"
        ))
    }
  )

exportJars := true

resolvers += "jitpack" at "https://jitpack.io"
