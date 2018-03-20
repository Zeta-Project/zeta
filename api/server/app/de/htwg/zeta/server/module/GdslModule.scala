package de.htwg.zeta.server.module

import javax.inject.Singleton

import com.google.inject.Provides
import de.htwg.zeta.common.format.project.gdsl.DiagramsFormat
import de.htwg.zeta.common.format.project.gdsl.StylesFormat
import de.htwg.zeta.common.format.project.gdsl.shape.ShapeFormat
import net.codingwell.scalaguice.ScalaModule

class GdslModule extends ScalaModule {

  override def configure(): Unit = {}

  @Provides
  @Singleton
  def provideShapeFormat(): ShapeFormat = ShapeFormat()

  @Provides
  @Singleton
  def provideShapeFormat(): ShapeFormat = ShapeFormat()

  @Provides
  @Singleton
  def provideDiagramsFormat(): DiagramsFormat = DiagramsFormat()

  @Provides
  @Singleton
  def provideStylesFormat(): StylesFormat = StylesFormat()


}
