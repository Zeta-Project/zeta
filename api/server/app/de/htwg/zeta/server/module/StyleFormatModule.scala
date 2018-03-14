package de.htwg.zeta.server.module

import javax.inject.Singleton

import com.google.inject.Provides
import de.htwg.zeta.common.format.project.gdsl.StylesFormat
import de.htwg.zeta.common.format.project.gdsl.style.BackgroundFormat
import de.htwg.zeta.common.format.project.gdsl.style.ColorFormat
import de.htwg.zeta.common.format.project.gdsl.style.FontFormat
import de.htwg.zeta.common.format.project.gdsl.style.LineFormat
import de.htwg.zeta.common.format.project.gdsl.style.StyleFormat
import net.codingwell.scalaguice.ScalaModule

class StyleFormatModule extends ScalaModule {

  override def configure(): Unit = {
    bind[ColorFormat].toInstance(new ColorFormat)
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
