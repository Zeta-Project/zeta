
lazy val parser = ZetaBuild.defaultProject(project).settings(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
    "org.scalaz" %% "scalaz-core" % "7.3.2"
  )
).dependsOn(
  ZetaBuild.common
)
