name := "Scala SBT Template"

version := "0.1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
	"com.typesafe.play" %% "play-json" % "2.5.1",
	//"com.typesafe.play" %% "play-ws" % "2.5.1",
	"com.typesafe.play" %% "play-ws" % "2.4.0-M2",
	"com.typesafe" % "config" % "1.3.0",
	"org.reflections" % "reflections" % "0.9.10",
	"org.scala-lang.modules" % "scala-swing_2.11" % "1.0.1", // only for Reactor/Publisher
	"com.github.scopt" %% "scopt" % "3.4.0"
)

resolvers += Resolver.sonatypeRepo("public")

mainClass := Some("zeta.generator.Main")

scalacOptions ++= Seq("-unchecked", "-deprecation")
