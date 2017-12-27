lazy val server = ZetaBuild.defaultProject(project).settings(
  name := "api",
  version := "0.1",

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
    // silhouette
    "com.mohiva" %% "play-silhouette" % ZetaBuild.silhouetteVersion,
    "com.mohiva" %% "play-silhouette-password-bcrypt" % ZetaBuild.silhouetteVersion,
    "com.mohiva" %% "play-silhouette-persistence" % ZetaBuild.silhouetteVersion,
    "com.mohiva" %% "play-silhouette-crypto-jca" % ZetaBuild.silhouetteVersion,

    //typesafe
    "org.webjars" %% "webjars-play" % "2.6.2",
    "com.iheart" %% "ficus" % "1.4.3",
    "com.typesafe.play" %% "play-mailer" % "6.0.1",
    "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
    "com.typesafe.play" %% "play-guice" % ZetaBuild.playVersion,
    "com.typesafe.play" %% "play-ws" % ZetaBuild.playVersion,
    "com.typesafe.play" %% "play-cache" % ZetaBuild.playVersion,
    "com.typesafe.play" %% "play-ehcache" % ZetaBuild.playVersion,
    "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3", // used in play for bootstrap integration
    "com.typesafe.play" %% "filters-helpers" % "2.5.0",
    "com.typesafe.akka" %% "akka-actor" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % ZetaBuild.akkaVersion,
    // "com.mohiva" %% "play-silhouette-testkit" % ZetaBuild.silhouetteVersion % "test",  // used for play integration testing
    //"com.typesafe.play" %% "play-specs2"% "2.5.9" % "test",  // used for play integration testing

    //scala
    "org.scala-lang" % "scala-reflect" % ZetaBuild.scalaVersionNumber,
    "org.scala-lang" % "scala-compiler" % ZetaBuild.scalaVersionNumber,

    // quicklens
    "com.softwaremill.quicklens" %% "quicklens" % "1.4.8"
  )
).enablePlugins(PlayScala).dependsOn(
  ZetaBuild.common,
  ZetaBuild.generatorControl,
  ZetaBuild.persistence
)
