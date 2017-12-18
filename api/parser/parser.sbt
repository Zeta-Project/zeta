
lazy val parser = ZetaBuild.defaultProject(project).settings(
  libraryDependencies ++= Seq(
    // parser combinator
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
    // scalaz
    "org.scalaz" %% "scalaz-core" % "7.2.8"
  )
).dependsOn(ZetaBuild.server)