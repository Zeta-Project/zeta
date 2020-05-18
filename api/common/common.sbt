
lazy val common = ZetaBuild.defaultProject(project).settings(
  version := "0.1",
  resolvers += Resolver.jcenterRepo,

  libraryDependencies ++= Seq(
    // play json. Should be fixed in the future, see https://github.com/playframework/play-json/issues/236
    "com.typesafe.play" %% "play-json" % "2.6.10",
    // akka actor
    "com.typesafe.akka" %% "akka-actor" % ZetaBuild.akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % ZetaBuild.akkaVersion,
    // rxScala
    "io.reactivex" %% "rxscala" % "0.26.5",
    // websocket
    "com.neovisionaries" % "nv-websocket-client" % "2.3"


  )
)
