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

lazy val generatorControl = projectT("generatorControl", file(".")).settings(
  Seq(
    name                   := "generatorControl",
    version                := "0.1",
    packageName in Docker  := "generatorControl",
    daemonUser in Docker   := "root",
    libraryDependencies   ++= Seq(
      "org.iq80.leveldb"          % "leveldb"                   % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all"            % "1.8",
      "com.typesafe.akka"         %% "akka-testkit"             % akkaVersion     % "test",
      "org.scalatest"             %% "scalatest"                % "2.2.4"         % "test",
      "com.typesafe.akka"         %% "akka-remote"              % akkaVersion,
      "com.typesafe.akka"         %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka"         %% "akka-http-core"           % "10.0.6",
      "com.typesafe.akka"         %% "akka-http-testkit"        % "10.0.6",
      "com.typesafe.akka"         %% "akka-cluster-sharding"    % akkaVersion,
      "com.spotify"               % "docker-client"             % "6.1.1",
      "commons-io"                % "commons-io"                % "2.4"           % "test",
      "org.rogach"                %% "scallop"                  % "2.0.2",
      "com.github.romix.akka"     %% "akka-kryo-serialization"  % "0.4.1",
      "com.neovisionaries"        % "nv-websocket-client"       % "1.30"
    )
  )
).dependsOn(ZetaBuild.common).dependsOn(ZetaBuild.persistence)
