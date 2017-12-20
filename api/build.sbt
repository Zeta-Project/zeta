organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"

// Move into project server on startup
onLoad in Global := (onLoad in Global).value.andThen(state => Command.process("project server", state))

lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server

// TODO  remove this in the future
lazy val images = project
