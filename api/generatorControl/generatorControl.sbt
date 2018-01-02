
lazy val generatorControl = ZetaBuild.defaultProject(project).settings(
  name := "generatorControl",
  version := "0.1",
  fork := true, // this is needed for akka-kryo-serialization
  libraryDependencies ++= Seq(
    // akka
    "com.typesafe.akka" %% "akka-cluster-metrics" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % ZetaBuild.akkaVersion,
    // this is needed in akka persistence
    "org.iq80.leveldb" % "leveldb" % "0.10",
    // docker client
    "com.spotify" % "docker-client" % "6.1.1",
    // scallop
    "org.rogach" %% "scallop" % "3.1.1", // migration guide: https://github.com/scallop/scallop/wiki/Migration-notes
    //play ws
    "com.typesafe.play" %% "play-ahc-ws" % ZetaBuild.playVersion,
    // kryo serialization
    "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.2"
  )
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
).dependsOn(
  ZetaBuild.common,
  ZetaBuild.persistence
)
