
lazy val parser = ZetaBuild.defaultProject(project).settings(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
    "org.scalaz" %% "scalaz-core" % "7.2.18"
  )
).dependsOn(
  ZetaBuild.common
)