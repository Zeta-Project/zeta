name := "modigen_v3"

version := "1.0"

lazy val `modigen_v3` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

/** Scala Dependencies */
libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.mongodb" %% "casbah" % "2.7.3",
  "com.novus" %% "salat" % "1.9.9",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT"
)

/** WebJar Javascript Dependencies */
libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "polymer" % "1.0.6",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "webcomponentsjs" % "0.7.2"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  