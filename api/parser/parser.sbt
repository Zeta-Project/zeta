
lazy val parser = ZetaBuild.defaultProject(project).settings(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
    "org.scalaz" %% "scalaz-core" % "7.2.30"
  )
).dependsOn(
  ZetaBuild.common
)