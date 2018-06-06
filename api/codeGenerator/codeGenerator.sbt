
lazy val codeGenerator = ZetaBuild.defaultProject(project).settings(
  name := "codeGenerator",
  version := "0.1",
  libraryDependencies ++= Seq(
  )
).enablePlugins(
  SbtTwirl
).dependsOn(
  ZetaBuild.common,
  ZetaBuild.persistence
)
