package de.htwg.zeta.parser

import com.google.inject.AbstractModule
import grizzled.slf4j.Logging
import net.codingwell.scalaguice.ScalaModule

class ParserModule extends AbstractModule with ScalaModule with Logging {

  override def configure(): Unit = {
    bind[GraphicalDSLParser]
  }

}
