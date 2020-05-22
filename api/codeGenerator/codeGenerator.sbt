
lazy val codeGenerator = ZetaBuild.defaultProject(project).settings(
  name := "codeGenerator",
  version := "0.1",
  libraryDependencies ++= Seq(
    "org.scalariform" %% "scalariform" % "0.2.10"
  )
).settings(
  scalastyleFailOnError := false
).enablePlugins(
  SbtTwirl
).dependsOn(
  ZetaBuild.common,
  ZetaBuild.persistence
)
