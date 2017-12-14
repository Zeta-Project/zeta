lazy val akkaVersion = "2.4.18"

lazy val persistence = ZetaBuild.inCurrent(project).settings(
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
  ),

  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % "2.5.7",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  ),
  fork := true,
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0"
  ),
  ZetaBuild.scalaOptions,

  scalastyleFailOnError := true,
  ZetaBuild.compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
  compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
  wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

  dockerRepository := Some("modigen")

).dependsOn(
  ZetaBuild.common
).enablePlugins(
  JavaAppPackaging,
  DockerSpotifyClientPlugin
)
