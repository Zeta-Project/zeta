
def image(name: String, d: sbt.File) = {
  Project(name, d).settings(
    ZetaBuild.scalaOptions,
    ZetaBuild.scalaVersion,
    dockerRepository := Some("modigen"),
    dockerBaseImage := "openjdk:8u151-jre-stretch",
    fork := true,
    libraryDependencies ++= Seq(
      "org.clapper" %% "grizzled-slf4j" % "1.2.0",
      "org.slf4j" % "slf4j-simple" % "1.7.9",
      "org.rogach" %% "scallop" % "3.1.1", // migration guide: https://github.com/scallop/scallop/wiki/Migration-notes
      "com.typesafe.play" %% "play-ws" % ZetaBuild.playVersion,
      "com.typesafe.play" %% "play-ahc-ws" % ZetaBuild.playVersion,
      "org.scala-lang" % "scala-reflect" % ZetaBuild.scalaVersionNumber,
      "org.scala-lang" % "scala-compiler" % ZetaBuild.scalaVersionNumber
    )
  ).enablePlugins(JavaAppPackaging).enablePlugins(DockerSpotifyClientPlugin).dependsOn(ZetaBuild.common).dependsOn(ZetaBuild.persistence)
}


lazy val scalaFilter = image("scalaFilter", file("./filter/scala")).settings(
  name := "filter/scala",
  version := "0.1",
  packageName in Docker := "filter/scala"
)

lazy val basicGenerator = image("basicGenerator", file("./generator/basic")).settings(
  name := "generator/basic",
  version := "0.1",
  packageName in Docker := "generator/basic"
)

lazy val metaModelRelease = image("metaModelRelease", file("./metamodel/release")).settings(
  name := "metamodel/release",
  version := "0.1",
  packageName in Docker := "metamodel/release"
)
