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
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0",

    // silhouette
    "com.mohiva" %% "play-silhouette" % "4.0.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",

    //injection
    "net.codingwell" %% "scala-guice" % "4.0.1",

    //typesafe
    "org.webjars" %% "webjars-play" % "2.5.0-2",
    "com.iheart" %% "ficus" % "1.2.6",
    "com.typesafe.play" %% "play-mailer" % "5.0.0",
    "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3", // used in play for bootstrap integration
    "com.typesafe.play" %% "filters-helpers" % "2.5.0",
    "com.typesafe.akka" %% "akka-actor" % "2.4.18",
    "com.typesafe.akka" %% "akka-kernel" % "2.4.18",
    "com.typesafe.akka" %% "akka-cluster" % "2.4.18",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.18",
    // "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",  // used for play integration testing
    //"com.typesafe.play" %% "play-specs2"% "2.5.9" % "test",  // used for play integration testing

    //rhino
    "org.mozilla" % "rhino" % "1.7.6",

    //scala
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scala-lang" % "scala-compiler" % "2.11.8",

    // quicklens
    "com.softwaremill.quicklens" %% "quicklens" % "1.4.8"
  ),
  fork := true,
  scalaVersion := "2.11.7",

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
