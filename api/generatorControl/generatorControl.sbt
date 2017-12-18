lazy val akkaVersion = "2.4.18"


lazy val generatorControl = ZetaBuild.defaultProject(project).settings(
  fork := true,
  libraryDependencies ++= Seq(
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0"
  ),
  dockerRepository := Some("modigen"),

  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.5.7",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  ),
  name := "generatorControl",
  version := "0.1",
  packageName in Docker := "generatorControl",
  daemonUser in Docker := "root",
  libraryDependencies ++= Seq(
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % "10.0.6",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6",
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.spotify" % "docker-client" % "6.1.1",
    "commons-io" % "commons-io" % "2.4" % "test",
    "org.rogach" %% "scallop" % "2.0.2",
    "com.github.romix.akka" %% "akka-kryo-serialization" % "0.4.1",
    "com.neovisionaries" % "nv-websocket-client" % "1.30"
  )
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
).dependsOn(
  ZetaBuild.common,
  ZetaBuild.persistence
)
