organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"

// run in project api will now run project server
run := (run in server in Compile).evaluated

lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val codeGenerator = ZetaBuild.codeGenerator
lazy val parser = ZetaBuild.parser
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server.aggregate(common, generatorControl, parser, persistence, codeGenerator)

// TODO  remove this in the future
lazy val images = project
