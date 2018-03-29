organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"

lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val parser = ZetaBuild.parser
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server.aggregate(common, generatorControl, parser, persistence)

// TODO  remove this in the future
lazy val images = project
