
lazy val parser = ZetaBuild.inCurrent(project).settings(
  scalaVersion := "2.11.7",
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
  ZetaBuild.compileScalastyle := scalastyle.in(Compile).toTask("").value,
  compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
  wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

  libraryDependencies ++= Seq(
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.2.0",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6"
  )
).dependsOn(ZetaBuild.server)