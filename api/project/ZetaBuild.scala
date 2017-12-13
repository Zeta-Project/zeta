import sbt._

object ZetaBuild {

  lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

  lazy val common = project
  lazy val generatorControl = project
  lazy val persistence = project
  lazy val server = project

}
