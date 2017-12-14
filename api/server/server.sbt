lazy val akkaVersion = "2.4.18"

lazy val server = ZetaBuild.inCurrent(project).settings(
  // docker settings
  name := "api",
  version := "0.1",
  packageName in Docker := "api",
  daemonUser in Docker := "root",

  wartremoverExcluded += crossTarget.value / "routes" / "main" / "router" / "Routes.scala",
  wartremoverExcluded += crossTarget.value / "routes" / "main" / "router" / "RoutesPrefix.scala",
  wartremoverExcluded += crossTarget.value / "routes" / "main" / "controllers" / "ReverseRoutes.scala",
  wartremoverExcluded += crossTarget.value / "routes" / "main" / "controllers" / "javascript" / "JavaScriptReverseRoutes.scala",

  routesGenerator := InjectedRoutesGenerator,
  routesImport += "de.htwg.zeta.server.util.route.Binders._",

  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  resolvers += Resolver.jcenterRepo,
  wartremoverErrors += Wart.AsInstanceOf,

  libraryDependencies ++= Seq(
    // codec
    "commons-codec" % "commons-codec" % "1.9",
    // silhouette
    "com.mohiva" %% "play-silhouette" % "4.0.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
    "org.webjars" %% "webjars-play" % "2.5.0-2",
    "net.codingwell" %% "scala-guice" % "4.0.1",
    "com.iheart" %% "ficus" % "1.2.6",
    "com.typesafe.play" %% "play-mailer" % "5.0.0",
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.5.0-akka-2.4.x",
    "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",
    "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",
    specs2 % Test,
    cache,
    ws,

    "com.novus" %% "salat" % "1.9.9",
    "com.lihaoyi" %% "upickle" % "0.3.4",
    "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-kernel" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "org.webjars" %% "webjars-play" % "2.4.0-1",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",

    "com.nulab-inc" %% "play2-oauth2-provider" % "0.15.1",
    "org.mozilla" % "rhino" % "1.7.6",
    "net.codingwell" %% "scala-guice" % "4.0.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.12.3",
    "org.scala-lang" % "scala-swing" % "2.11.0-M7",
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scala-lang" % "scala-compiler" % "2.11.8",
    "com.softwaremill.quicklens" %% "quicklens" % "1.4.8"
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
).enablePlugins(PlayScala).dependsOn(
  ZetaBuild.common,
  ZetaBuild.generatorControl,
  ZetaBuild.persistence
)
