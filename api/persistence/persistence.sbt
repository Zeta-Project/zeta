lazy val persistence = ZetaBuild.inCurrent(project).settings(
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
    // akka actor system
    "com.typesafe.akka" %% "akka-actor" %  "2.4.18",
    // injection
    "net.codingwell" %% "scala-guice" % "4.0.1",
    // test
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0"
  ),
  scalaVersion := "2.11.7",

  ZetaBuild.scalaOptions,

  scalastyleFailOnError := true,
  ZetaBuild.compileScalastyle := scalastyle.in(Compile).toTask("").value,
  compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
  wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements)

).dependsOn(
  ZetaBuild.common
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
)
