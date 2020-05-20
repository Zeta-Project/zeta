import sbt.Def
import sbt.Keys
import sbt.Keys.scalacOptions
import sbt.Project
import sbt.file
import sbt.project
import sbt.stringToOrganization
import sbt.taskKey
import sbt.addCompilerPlugin
import wartremover.WartRemover.autoImport.Wart
import wartremover.WartRemover.autoImport.Warts
import wartremover.WartRemover.autoImport.wartremoverWarnings

object ZetaBuild {

  lazy val common = project
  lazy val generatorControl = project
  lazy val codeGenerator = project
  lazy val parser = project
  lazy val persistence = project
  lazy val server = project

  val compileScalastyle = taskKey[Unit]("compileScalastyle")
  val silhouetteVersion = "5.0.7"
  val playVersion = "2.6.25"
  val akkaVersion = "2.5.31"
  val javaFxVersion = "11"
  val scalaFxVersion = "11-R16"

  val scalaVersionNumber = "2.12.11"
  val scalaVersion = Keys.scalaVersion := scalaVersionNumber

  val scalaOptions = scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    // "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    // FIXME This is a confirmed Scala bug in 2.12 Xlint will produce Position.point on NoPosition Error
    // "-Xlint", // Enable recommended additional warnings.
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
    "-Ywarn-numeric-widen" // Warn when numerics are widened.
  )

  val autoCompilerPlugins = true

  val linterSettings = Seq(
    wartremoverWarnings ++= Warts.unsafe diff List(
      Wart.NonUnitStatements,
      Wart.Any,
      Wart.Product,
    )
  )
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux")   => "linux"
    case n if n.startsWith("Mac")     => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }

  lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")

  val javaFxDep = javaFXModules.map( m =>
    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier osName
  )

  val standardLibraries = Keys.libraryDependencies ++= Seq(
    // injection
    "net.codingwell" %% "scala-guice" % "4.1.1",
    // test
    "org.scalatest" %% "scalatest" % "3.1.1" % "test",
    // logging
    "org.clapper" %% "grizzled-slf4j" % "1.3.4",
    // javafx
    "org.scalafx" %% "scalafx" % scalaFxVersion
  ) ++ javaFxDep



  val defaultSettings: Seq[Def.SettingsDefinition] = linterSettings ++ Seq(
    scalaOptions,
    scalaVersion,
    standardLibraries
  )

  /**
   * change project to current file
   */
  def inCurrent(project: Project): Project = project in file(".")

  /**
   * change project to current file and add defaultSettings
   */
  def defaultProject(project: Project): Project = inCurrent(project).settings(defaultSettings: _*)


}
