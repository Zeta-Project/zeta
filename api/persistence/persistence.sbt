lazy val persistence = ZetaBuild.defaultProject(project).settings(
  name := "persistence",
  version := "0.1",

  libraryDependencies ++= Seq(
    // json parser
    "com.typesafe.play" %% "play-json" % "2.5.7",
    // sillhouette
    "com.mohiva" %% "play-silhouette" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
    // mongoDB accessor
    "org.reactivemongo" %% "reactivemongo" % "0.12.3",
    "org.reactivemongo" %% "reactivemongo-play-json" % "0.12.3",
    // akka actor system
    "com.typesafe.akka" %% "akka-actor" %  "2.4.18",
    // injection
    "net.codingwell" %% "scala-guice" % "4.0.1",
    // test
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0"
  )
).dependsOn(
  ZetaBuild.common
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
)
