organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"

// Move into project server on startup
onLoad in Global := (onLoad in Global).value.andThen(state => Command.process("project server", state))

lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server

// TODO  remove this in the future
lazy val images = project


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
    compileScalastyle := scalastyle.in(Compile).toTask("").value,
    compile in Compile := ((compile in Compile) dependsOn compileScalastyle).value,
    wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

    dockerRepository := Some("modigen")
  )
}

def baseProject(name: String, d: sbt.File) = Project(name, d).settings(baseSettings)



lazy val parser = baseProject("parser", file("parser")).settings(
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6"
  )
).dependsOn(server)