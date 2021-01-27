organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"
packageSummary := "Backend for generating graphical modeling environments"
packageDescription := """
 |Zeta is a tool for automatically generating a graphical DSL from a range of text DSLs.
 |In combination with a suitable meta-model, you render these text definitions for a generator that creates a graphical editor for the web.
 |So it can bee seen as a Model-driven generation of graphical editors with the goal to generate simulations from a graphical DSL.""".stripMargin

// run in project api will now run project server
run := (run in server in Compile).evaluated

enablePlugins(JDKPackagerPlugin)
enablePlugins(JavaServerAppPackaging)
enablePlugins(LinuxPlugin)

lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val codeGenerator = ZetaBuild.codeGenerator
lazy val parser = ZetaBuild.parser
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server.aggregate(common, generatorControl, parser, persistence, codeGenerator)

// TODO  remove this in the future
lazy val images = project