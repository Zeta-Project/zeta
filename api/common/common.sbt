lazy val akkaVersion = "2.4.18"

lazy val common = ZetaBuild.inCurrent(project).settings(
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

    dockerRepository := Some("modigen"),

    version := "0.1",
    resolvers += Resolver.jcenterRepo,

    libraryDependencies ++= Seq(
      // silhouette
      "com.mohiva" %% "play-silhouette" % "4.0.0",
      "com.typesafe.play" %% "play-json" % "2.5.7",
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "io.reactivex" % "rxscala_2.11" % "0.26.2",
      "org.julienrf" %% "play-json-derived-codecs" % "3.3",
      "com.typesafe.play" %% "play-ws" % "2.5.9",
      "com.neovisionaries" % "nv-websocket-client" % "1.30",
      "org.scalaz" %% "scalaz-core" % "7.2.8",
      "com.github.blemale" %% "scaffeine" % "2.0.0" % "compile",
      "org.reactivemongo" %% "reactivemongo" % "0.12.3",
      "com.typesafe.play" %% "play-json" % "2.5.4"
    )
)
