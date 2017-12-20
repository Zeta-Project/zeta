lazy val akkaVersion = "2.4.18"

lazy val common = ZetaBuild.defaultProject(project).settings(
  version := "0.1",
  resolvers += Resolver.jcenterRepo,

  libraryDependencies ++= Seq(
    // silhouette
    "com.mohiva" %% "play-silhouette" % ZetaBuild.silhouetteVersion,
    // play json
    "com.typesafe.play" %% "play-json" % "2.5.7",
    // akka actor
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
    // rxScala
    "io.reactivex" %% "rxscala" % "0.26.5",
    // websocket
    "com.neovisionaries" % "nv-websocket-client" % "1.30"
  )
)
