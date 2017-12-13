
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
    ZetaBuild.compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
    compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
    wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements),

    dockerRepository := Some("modigen")
  )
}

def baseProject(name: String, d: sbt.File) = Project(name, d).settings(baseSettings)


def image(name: String, d: sbt.File) = {
  baseProject(name, d).settings(
    Seq(
      libraryDependencies ++= Seq(
        "org.rogach" %% "scallop" % "2.0.2",
        "org.scala-lang" % "scala-reflect" % "2.11.8",
        "org.scala-lang" % "scala-compiler" % "2.11.8"
      )
    )
  ).enablePlugins(JavaAppPackaging).enablePlugins(DockerSpotifyClientPlugin).dependsOn(ZetaBuild.common).dependsOn(ZetaBuild.persistence)
}


lazy val scalaFilter = image("scalaFilter", file("./filter/scala")).settings(
  Seq(
    name := "filter/scala",
    version := "0.1",
    packageName in Docker := "filter/scala"
  )
)

lazy val scalaGeneratorTemplate = image("template", file("./generator/template")).settings()


lazy val basicGenerator = image("basicGenerator", file("./generator/basic")).settings(
  Seq(
    name := "generator/basic",
    version := "0.1",
    packageName in Docker := "generator/basic"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val fileGenerator = image("fileGenerator", file("./generator/file")).settings(
  Seq(
    name                   := "generator/file",
    version                := "0.1",
    packageName in Docker  := "generator/file"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val remoteGenerator = image("remoteGenerator", file("./generator/remote")).settings(
  Seq(
    name                   := "generator/remote",
    version                := "0.1",
    packageName in Docker  := "generator/remote"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val specificGenerator = image("specificGenerator", file("./generator/specific")).settings(
  Seq(
    name                   := "generator/specific",
    version                := "0.1",
    packageName in Docker  := "generator/specific"
  )
).dependsOn(scalaGeneratorTemplate)

lazy val metaModelRelease = image("metaModelRelease", file("./metamodel/release")).settings(
  Seq(
    name                   := "metamodel/release",
    version                := "0.1",
    packageName in Docker  := "metamodel/release"
  )
)
