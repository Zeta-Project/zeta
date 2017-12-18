// Comment to get more information during initialization
logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")

libraryDependencies += "com.spotify" % "docker-client" % "3.5.13"
