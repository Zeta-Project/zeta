lazy val akkaVersion = "2.4.18"


lazy val generatorControl = ZetaBuild.defaultProject(project).settings(
  name := "generatorControl",
  version := "0.1",
  fork := true, // this is needed for akka-kryo-serialization
  libraryDependencies ++= Seq(
    // akka
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    // this is needed in akka persistence
    "org.iq80.leveldb" % "leveldb" % "0.7",
    // docker client
    "com.spotify" % "docker-client" % "6.1.1",
    // scallop
    "org.rogach" %% "scallop" % "2.0.2",
    // kryo serialization
    "com.github.romix.akka" %% "akka-kryo-serialization" % "0.4.1"
  )
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
).dependsOn(
  ZetaBuild.common,
  ZetaBuild.persistence
)
