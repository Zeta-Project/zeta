package de.htwg.zeta.server.module

import de.htwg.zeta.common.format.project.TaskResultFormat
import de.htwg.zeta.common.format.project.gdsl.DiagramsFormat
import de.htwg.zeta.common.format.project.gdsl.StylesFormat
import de.htwg.zeta.common.format.project.gdsl.shape.ShapeFormat
import net.codingwell.scalaguice.ScalaModule

class GdslModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ShapeFormat].toInstance(ShapeFormat())
    bind[DiagramsFormat].toInstance(DiagramsFormat())
    bind[StylesFormat].toInstance(StylesFormat())
    bind[TaskResultFormat].toInstance(TaskResultFormat())
  }

}
