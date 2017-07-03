package de.htwg.zeta.generatorControl

import scala.language.implicitConversions

import de.htwg.zeta.generatorControl.start.DummyStarter
import de.htwg.zeta.generatorControl.start.Commands
import de.htwg.zeta.generatorControl.start.MasterStarter
import de.htwg.zeta.generatorControl.start.WorkersStarter
import de.htwg.zeta.generatorControl.start.DeveloperStarter


object Main extends App {
  val cmd = new Commands(args)


  WorkersStarter(cmd).foreach(_.start())
  MasterStarter(cmd).foreach(_.start())
  DeveloperStarter(cmd).foreach(_.start())
  DummyStarter(cmd).foreach(_.start())

}






















