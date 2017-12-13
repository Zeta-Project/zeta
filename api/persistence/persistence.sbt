lazy val akkaVersion = "2.4.18"

def baseSettings = {
  Revolver.settings ++ Seq(
    fork := true,
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      // logging
      "org.clapper" %% "grizzled-slf4j" % "1.2.0"
    ),
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      // "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint", // Enable recommended additional warnings.
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
    ),

    scalastyleFailOnError := true,
    ZetaBuild.compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
    compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
    wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

    dockerRepository := Some("modigen")
  )
}

def baseProject(name: String, d: sbt.File) = Project(name, d).settings(baseSettings)


def projectT(name: String, d: sbt.File) = {
  baseProject(name, d).settings(
    Seq(
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-json" % "2.5.7",
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
        "com.typesafe.akka" %% "akka-persistence" % akkaVersion
      )
    )
  ).enablePlugins(JavaAppPackaging).enablePlugins(DockerSpotifyClientPlugin)
}

lazy val persistence = projectT("persistence", file(".")).settings(
  Seq(
    name := "persistence",
    version := "0.1",
    // packageName in Docker  := "persistence",
    // daemonUser in Docker   := "root",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "com.mohiva" %% "play-silhouette" % "4.0.0",
      "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
      "org.reactivemongo" %% "reactivemongo" % "0.12.3",
      "org.reactivemongo" %% "reactivemongo-play-json" % "0.12.3",
      "net.codingwell" %% "scala-guice" % "4.0.1"
    )
  )
).dependsOn(ZetaBuild.common)

