name := "zeta-api"
version := "1.0.0"

// Move into project server on startup
onLoad in Global := (onLoad in Global).value.andThen(state => Command.process("project server", state))

lazy val akkaVersion = "2.4.18"
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

def baseSettings = {
  Revolver.settings ++ Seq(
    fork := true,
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      // logging
      "org.clapper" %% "grizzled-slf4j" % "1.2.0"
    ),
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      // "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint", // Enable recommended additional warnings.
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.
    ),

    scalastyleFailOnError := true,
    compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
    compile in Compile := ((compile in Compile) dependsOn compileScalastyle).value,
    wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

    dockerRepository := Some("modigen")
  )
}

def baseProject(name: String, d: sbt.File) = Project(name, d).settings(baseSettings)

lazy val server = baseProject("server", file("server")).settings(
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
  )
).enablePlugins(PlayScala).dependsOn(common).dependsOn(generatorControl).dependsOn(persistence)


lazy val common = baseProject("common", file("common")).settings(
  Seq(
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
)

def projectT(name: String, d: sbt.File) = {
  baseProject(name, d).settings(
    Seq(
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play-json" % "2.5.7",
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
        "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
        "com.typesafe.akka" %% "akka-persistence" % akkaVersion
      )
    )
  ).enablePlugins(JavaAppPackaging).enablePlugins(DockerSpotifyClientPlugin)
}

lazy val generatorControl = projectT("generatorControl", file("generatorControl")).settings(
  Seq(
    name                   := "generatorControl",
    version                := "0.1",
    packageName in Docker  := "generatorControl",
    daemonUser in Docker   := "root",
    libraryDependencies   ++= Seq(
      "org.iq80.leveldb"          % "leveldb"                   % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all"            % "1.8",
      "com.typesafe.akka"         %% "akka-testkit"             % akkaVersion     % "test",
      "org.scalatest"             %% "scalatest"                % "2.2.4"         % "test",
      "com.typesafe.akka"         %% "akka-remote"              % akkaVersion,
      "com.typesafe.akka"         %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka"         %% "akka-http-core"           % "10.0.6",
      "com.typesafe.akka"         %% "akka-http-testkit"        % "10.0.6",
      "com.typesafe.akka"         %% "akka-cluster-sharding"    % akkaVersion,
      "com.spotify"               % "docker-client"             % "6.1.1",
      "commons-io"                % "commons-io"                % "2.4"           % "test",
      "org.rogach"                %% "scallop"                  % "2.0.2",
      "com.github.romix.akka"     %% "akka-kryo-serialization"  % "0.4.1",
      "com.neovisionaries"        % "nv-websocket-client"       % "1.30"
    )
  )
).dependsOn(common).dependsOn(persistence)

lazy val persistence = projectT("persistence", file("persistence")).settings(
  Seq(
    name := "persistence",
    version := "0.1",
    // packageName in Docker  := "persistence",
    // daemonUser in Docker   := "root",
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "com.mohiva" %% "play-silhouette" % "4.0.0",
      "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
      "org.reactivemongo" %% "reactivemongo" % "0.12.3",
      "org.reactivemongo" %% "reactivemongo-play-json" % "0.12.3",
      "net.codingwell" %% "scala-guice" % "4.0.1"
    )
  )
).dependsOn(common)


def image(name: String, d: sbt.File) = {
  baseProject(name, d).settings(
    Seq(
      libraryDependencies ++= Seq(
        "org.rogach" %% "scallop" % "2.0.2",
        "org.scala-lang" % "scala-reflect" % "2.11.8",
        "org.scala-lang" % "scala-compiler" % "2.11.8"
      )
    )
  ).enablePlugins(JavaAppPackaging).enablePlugins(DockerSpotifyClientPlugin).dependsOn(common).dependsOn(persistence)
}


lazy val scalaFilter = image("scalaFilter", file("./images/filter/scala")).settings(
  Seq(
    name := "filter/scala",
    version := "0.1",
    packageName in Docker := "filter/scala"
  )
)

lazy val scalaGeneratorTemplate = image("template", file("./images/generator/template")).settings()


lazy val basicGenerator = image("basicGenerator", file("./images/generator/basic")).settings(
  Seq(
    name := "generator/basic",
    version := "0.1",
    packageName in Docker := "generator/basic"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val fileGenerator = image("fileGenerator", file("./images/generator/file")).settings(
  Seq(
    name                   := "generator/file",
    version                := "0.1",
    packageName in Docker  := "generator/file"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val remoteGenerator = image("remoteGenerator", file("./images/generator/remote")).settings(
  Seq(
    name                   := "generator/remote",
    version                := "0.1",
    packageName in Docker  := "generator/remote"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val specificGenerator = image("specificGenerator", file("./images/generator/specific")).settings(
  Seq(
    name                   := "generator/specific",
    version                := "0.1",
    packageName in Docker  := "generator/specific"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val metaModelRelease = image("metaModelRelease", file("./images/metamodel/release")).settings(
  Seq(
    name                   := "metamodel/release",
    version                := "0.1",
    packageName in Docker  := "metamodel/release"
  )
)

