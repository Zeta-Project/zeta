lazy val akkaVersion = "2.4.18"

lazy val common = ZetaBuild.inCurrent(project).settings(
  scalaVersion := "2.11.7",

  ZetaBuild.scalaOptions,
  scalastyleFailOnError := true,
  ZetaBuild.compileScalastyle := scalastyle.in(Compile).toTask("").value,
  compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
  wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

  version := "0.1",
  resolvers += Resolver.jcenterRepo,

  libraryDependencies ++= Seq(
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0",
    // silhouette
    "com.mohiva" %% "play-silhouette" % "4.0.0",
    // play json
    "com.typesafe.play" %% "play-json" % "2.5.7",
    // akka actor
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    // rxScala
    "io.reactivex" %% "rxscala" % "0.26.2",
    // websocket
    "com.neovisionaries" % "nv-websocket-client" % "1.30"
  )
)
