import sbt.Project.projectToRef

lazy val clients = Seq(client)
lazy val scalaV = "2.11.6"

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  pipelineStages := Seq(scalaJSProd, gzip),
  libraryDependencies ++= Seq(
    filters,
    jdbc,
    "com.github.jahoefne" %% "scalot" % "0.4.4-SNAPSHOT",
    "org.mongodb" %% "casbah" % "2.7.3",
    "com.novus" %% "salat" % "1.9.9",
    "com.lihaoyi" %% "upickle" % "0.3.4",
    "ws.securesocial" % "securesocial_2.11" % "3.0-M3",
    "com.vmunier" %% "play-scalajs-scripts" % "0.2.1",
    "com.typesafe.akka" %% "akka-contrib" % "2.3.4",
    "com.typesafe.akka" %% "akka-actor" % "2.3.4",
    "com.typesafe.akka" %% "akka-kernel" % "2.3.4",
    "com.typesafe.akka" %% "akka-cluster" % "2.3.4",
    "org.webjars" %% "webjars-play" % "2.4.0-1",
    "org.webjars" % "font-awesome" % "4.1.0",
    "org.webjars" % "bootstrap" % "3.3.5",
    "org.webjars.bower" % "polymer" % "1.0.7",
    "org.webjars" % "jquery" % "2.1.4",
    "org.webjars" % "jquery-ui" % "1.11.4",
    "org.webjars" % "jquery-ui-themes" % "1.11.4",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    "org.webjars" % "typicons" % "2.0.7",
    "org.webjars.bower" % "bootbox.js" % "4.4.0",
    "com.nulab-inc" %% "play2-oauth2-provider" % "0.14.0",
    "io.argonaut" %% "argonaut" % "6.0.4"
  )).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  resolvers += "amateras-repo" at "http://amateras.sourceforge.jp/mvn-snapshot/",
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  persistLauncher in Test := false,
  sourceMapsDirectories += sharedJs.base / "..",
  libraryDependencies ++= Seq(
    "com.github.jahoefne" %%% "scalot" % "0.4.4-SNAPSHOT",
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.github.jahoefne" %%% "scalot" % "0.1",
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
    "com.lihaoyi" %%% "upickle" % "0.3.4"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV,
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies += "com.github.jahoefne" %%% "scalot" % "0.4.4-SNAPSHOT"
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false