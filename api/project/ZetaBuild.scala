import org.scalastyle.sbt.ScalastylePlugin.autoImport.scalastyle
import org.scalastyle.sbt.ScalastylePlugin.autoImport.scalastyleFailOnError
import sbt.taskKey
import sbt.Project
import sbt.project
import sbt.file
import sbt.Compile
import sbt.Keys
import sbt.Def
import sbt.Keys.scalacOptions
import sbt.Keys.compile
import wartremover.WartRemover.autoImport.Wart
import wartremover.WartRemover.autoImport.wartremoverWarnings
import wartremover.WartRemover.autoImport.Warts

object ZetaBuild {

  lazy val common = project
  lazy val generatorControl = project
  lazy val persistence = project
  lazy val server = project

  val compileScalastyle = taskKey[Unit]("compileScalastyle")

  val scalaOptions = scalacOptions ++= Seq(
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
  )

  val linterSettings = Seq(
    scalastyleFailOnError := true,
    ZetaBuild.compileScalastyle := scalastyle.in(Compile).toTask("").value,
    compile in Compile := ((compile in Compile) dependsOn ZetaBuild.compileScalastyle).value,
    wartremoverWarnings ++= Warts.unsafe.filterNot(_ == Wart.NonUnitStatements)
  )

  val scalaVersion = Keys.scalaVersion := "2.11.7"

  val defaultSettings: Seq[Def.SettingsDefinition] = linterSettings ++ Seq(
    scalaOptions,
    scalaVersion
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