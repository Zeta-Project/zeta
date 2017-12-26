lazy val persistence = ZetaBuild.defaultProject(project).settings(
  name := "persistence",
  version := "0.1",

  resolvers += Resolver.jcenterRepo,

  libraryDependencies ++= Seq(
    // json parser
    "com.typesafe.play" %% "play-json" % ZetaBuild.playVersion,
    // mongoDB accessor
    "org.reactivemongo" %% "reactivemongo" % "0.12.3",
    "org.reactivemongo" %% "reactivemongo-play-json" % "0.12.3",
    // akka actor system
    "com.typesafe.akka" %% "akka-actor" %  ZetaBuild.akkaVersion
  )
).dependsOn(
  ZetaBuild.common
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
)
