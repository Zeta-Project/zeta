
lazy val parser = ZetaBuild.defaultProject(project).settings(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.7",
    "org.scalaz" %% "scalaz-core" % "7.2.30"
  )
).settings(
  scalastyleFailOnError := false
).dependsOn(
  ZetaBuild.common
)