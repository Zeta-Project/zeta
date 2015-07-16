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
    "org.mongodb" %% "casbah" % "2.7.3",
    "com.novus" %% "salat" % "1.9.9",
    "com.lihaoyi" %% "upickle" % "0.2.8",
    "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
    "com.vmunier" %% "play-scalajs-scripts" % "0.2.1",
    "org.webjars" %% "webjars-play" % "2.3.0",
    "org.webjars" % "font-awesome" % "4.1.0",
    "org.webjars" % "polymer" % "1.0.6",
    "org.webjars" % "bootstrap" % "3.3.5",
    "org.webjars" % "webcomponentsjs" % "0.7.2",
    "org.webjars" % "jquery" % "2.1.4"
  )
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  sourceMapsDirectories += sharedJs.base / "..",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
    "com.lihaoyi" %%% "upickle" % "0.2.8"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false