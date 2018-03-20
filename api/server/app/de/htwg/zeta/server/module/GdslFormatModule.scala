package de.htwg.zeta.server.module

import javax.inject.Singleton

import com.google.inject.Provides
import de.htwg.zeta.common.format.project.gdsl.DiagramsFormat
import de.htwg.zeta.common.format.project.gdsl.StylesFormat
import de.htwg.zeta.common.format.project.gdsl.diagram.DiagramFormat
import de.htwg.zeta.common.format.project.gdsl.diagram.PaletteFormat
import de.htwg.zeta.common.format.project.gdsl.shape.EdgeFormat
import de.htwg.zeta.common.format.project.gdsl.shape.NodeFormat
import de.htwg.zeta.common.format.project.gdsl.shape.PlacingFormat
import de.htwg.zeta.common.format.project.gdsl.shape.PositionFormat
import de.htwg.zeta.common.format.project.gdsl.shape.ResizingFormat
import de.htwg.zeta.common.format.project.gdsl.shape.ShapeFormat
import de.htwg.zeta.common.format.project.gdsl.shape.SizeFormat
import de.htwg.zeta.common.format.project.gdsl.shape.geoModel.GeoModelFormat
import de.htwg.zeta.common.format.project.gdsl.style.BackgroundFormat
import de.htwg.zeta.common.format.project.gdsl.style.ColorFormat
import de.htwg.zeta.common.format.project.gdsl.style.FontFormat
import de.htwg.zeta.common.format.project.gdsl.style.LineFormat
import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import net.codingwell.scalaguice.ScalaModule

class GdslFormatModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ColorFormat].toInstance(new ColorFormat)
    bind[PaletteFormat].toInstance(new PaletteFormat)
    bind[PositionFormat].toInstance(new PositionFormat)
    bind[SizeFormat].toInstance(new SizeFormat)
    bind[ResizingFormat].toInstance(new ResizingFormat)
  }

  @Provides
  @Singleton
  def provideShapeFormat(
      edgeFormat: EdgeFormat,
      nodeFormat: NodeFormat
  ): ShapeFormat = {
    new ShapeFormat(nodeFormat, edgeFormat)
  }

  @Provides
  @Singleton
  def provideNodeFormat(
      styleFormat: StyleFormat,
      edgeFormat: EdgeFormat,
      sizeFormat: SizeFormat,
      resizingFormat: ResizingFormat,
      geoModelFormat: GeoModelFormat
  ): NodeFormat = {
    new NodeFormat(
      styleFormat,
      edgeFormat,
      sizeFormat,
      resizingFormat,
      geoModelFormat
    )
  }

  @Provides
  @Singleton
  def provideGeoModelFormat(): GeoModelFormat = {
    GeoModelFormat()
  }

  @Provides
  @Singleton
  def provideEdgeFormat(
      placingFormat: PlacingFormat
  ): EdgeFormat = {
    new EdgeFormat(placingFormat)
  }

  @Provides
  @Singleton
  def providePlacingFormat(
      styleFormat: StyleFormat,
      positionFormat: PositionFormat
  ): PlacingFormat = {
    new PlacingFormat(styleFormat, positionFormat)
  }

  @Provides
  @Singleton
  def provideDiagramsFormat(
      diagramFormat: DiagramFormat
  ): DiagramsFormat = {
    new DiagramsFormat(diagramFormat)
  }

  @Provides
  @Singleton
  def provideDiagramFormat(
      paletteFormat: PaletteFormat
  ): DiagramFormat = {
    new DiagramFormat(paletteFormat)
  }

  @Provides
  @Singleton
  def provideStylesFormat(
      styleFormat: StyleFormat
  ): StylesFormat = {
    new StylesFormat(styleFormat)
  }

  @Provides
  @Singleton
  def provideStyleFormat(
      backgroundFormat: BackgroundFormat,
      fontFormat: FontFormat,
      lineFormat: LineFormat
  ): StyleFormat = {
    new StyleFormat(backgroundFormat, fontFormat, lineFormat)
  }

  @Provides
  @Singleton
  def provideBackgroundFormat(
      colorFormat: ColorFormat
  ): BackgroundFormat = {
    new BackgroundFormat(colorFormat)
  }

  @Provides
  @Singleton
  def provideFontFormat(
      colorFormat: ColorFormat
  ): FontFormat = {
    new FontFormat(colorFormat)
  }

  @Provides
  @Singleton
  def provideLineFormat(
      colorFormat: ColorFormat
  ): LineFormat = {
    new LineFormat(colorFormat)
  }

}
