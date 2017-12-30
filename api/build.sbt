organization := "de.htwg"
name := "zeta-api"
version := "1.0.0"

val switchToServer: State => State =
  (state: State) =>
    state.copy(remainingCommands = Exec("project server", None) +: state.remainingCommands)


// Move into project server on startup
onLoad in Global := (onLoad in Global).value.andThen(switchToServer)


lazy val common = ZetaBuild.common
lazy val generatorControl = ZetaBuild.generatorControl
lazy val persistence = ZetaBuild.persistence
lazy val server = ZetaBuild.server

// TODO  remove this in the future
lazy val images = project
