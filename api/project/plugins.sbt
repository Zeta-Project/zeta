// Comment to get more information during initialization
logLevel := Level.Warn
resolvers += Resolver.sbtPluginRepo("releases")


resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"
resolvers += Resolver.bintrayIvyRepo("rtimush", "sbt-plugin-snapshots")


addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.25")

addSbtPlugin("com.lightbend.akka.grpc" % "sbt-akka-grpc" % "1.0.2")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.10")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")

// updates and dependency plugins
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.16")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.21")
