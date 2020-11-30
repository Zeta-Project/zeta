package de.htwg.zeta.server.module

import javax.inject.Singleton

import com.google.inject.Provides
import de.htwg.zeta.common.format.entity.BondedTaskFormat
import de.htwg.zeta.common.format.entity.EventDrivenTaskFormat
import de.htwg.zeta.common.format.entity.FileFormat
import de.htwg.zeta.common.format.entity.FilterFormat
import de.htwg.zeta.common.format.entity.GeneratorFormat
import de.htwg.zeta.common.format.entity.GeneratorImageFormat
import de.htwg.zeta.common.format.entity.TimedTaskFormat
import de.htwg.zeta.common.format.entity.UserFormat
import de.htwg.zeta.common.format.model.EdgeFormat
import de.htwg.zeta.common.format.model.GDSLInstanceProjectFormat
import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.model.NodeFormat
import de.htwg.zeta.common.format.project.AttributeFormat
import de.htwg.zeta.common.format.project.AttributeTypeFormat
import de.htwg.zeta.common.format.project.AttributeValueFormat
import de.htwg.zeta.common.format.project.ClassFormat
import de.htwg.zeta.common.format.project.ConceptFormat
import de.htwg.zeta.common.format.project.EnumFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.format.project.GraphicalDslReleaseFormat
import de.htwg.zeta.common.format.project.MethodFormat
import de.htwg.zeta.common.format.project.ReferenceFormat
import net.codingwell.scalaguice.ScalaModule

class JsonFormatModule extends ScalaModule {

  private val sString = "String"
  private val sBoolean = "Bool"
  private val sInt = "Int"
  private val sDouble = "Double"

  override def configure(): Unit = {
    bind[EventDrivenTaskFormat].toInstance(new EventDrivenTaskFormat)
    bind[BondedTaskFormat].toInstance(new BondedTaskFormat)
    bind[FileFormat].toInstance(new FileFormat)
    bind[FilterFormat].toInstance(new FilterFormat)
    bind[GeneratorImageFormat].toInstance(new GeneratorImageFormat(sSchema = s"$$schema", sRef = s"$$ref"))
    bind[GeneratorFormat].toInstance(new GeneratorFormat)
    bind[TimedTaskFormat].toInstance(new TimedTaskFormat)
    bind[AttributeTypeFormat].toInstance(new AttributeTypeFormat(sString = sString, sBoolean = sBoolean, sInt = sInt, sDouble = sDouble, sUnit = "Unit"))
    bind[AttributeValueFormat].toInstance(new AttributeValueFormat(sString = sString, sBoolean = sBoolean, sInt = sInt, sDouble = sDouble))
    bind[EnumFormat].toInstance(new EnumFormat)
    bind[UserFormat].toInstance(new UserFormat)
  }

  @Provides
  @Singleton
  def provideClassFormat(
      attributeFormat: AttributeFormat,
      methodFormat: MethodFormat
  ): ClassFormat = {
    new ClassFormat(attributeFormat, methodFormat)
  }

  @Provides
  @Singleton
  def provideReferenceFormat(
      attributeFormat: AttributeFormat,
      methodFormat: MethodFormat
  ): ReferenceFormat = {
    new ReferenceFormat(attributeFormat, methodFormat)
  }

  @Provides
  @Singleton
  def provideMetaModelFormat(
      enumFormat: EnumFormat,
      classFormat: ClassFormat,
      referenceFormat: ReferenceFormat,
      attributeFormat: AttributeFormat,
      methodFormat: MethodFormat
  ): ConceptFormat = {
    new ConceptFormat(enumFormat, classFormat, referenceFormat, attributeFormat, methodFormat)
  }

  @Provides
  @Singleton
  def provideMetaModelEntityFormat(
      metaModelFormat: ConceptFormat
  ): GdslProjectFormat = {
    new GdslProjectFormat(metaModelFormat)
  }

  @Provides
  @Singleton
  def provideMetaModelReleaseFormat(
      metaModelFormat: ConceptFormat
  ): GraphicalDslReleaseFormat = {
    new GraphicalDslReleaseFormat(metaModelFormat)
  }

  @Provides
  @Singleton
  def provideModelFormat(
      nodeFormat: NodeFormat,
      edgeFormat: EdgeFormat,
      attributeFormat: AttributeFormat,
      attributeValueFormat: AttributeValueFormat,
      methodFormat: MethodFormat
  ): GraphicalDslInstanceFormat = {
    new GraphicalDslInstanceFormat(nodeFormat, edgeFormat, attributeFormat, attributeValueFormat, methodFormat)
  }

  @Provides
  @Singleton
  def provideGDSLInstanceProjectFormat(
      gDSLInstanceFormat: GraphicalDslInstanceFormat,
      gDSLProjectFormat: GdslProjectFormat
  ): GDSLInstanceProjectFormat = {
    new GDSLInstanceProjectFormat(gDSLInstanceFormat, gDSLProjectFormat)
  }

  @Provides
  @Singleton
  def provideNodeFormat(
      attributeFormat: AttributeFormat,
      attributeValueFormat: AttributeValueFormat,
      methodFormat: MethodFormat
  ): NodeFormat = {
    new NodeFormat(attributeFormat, attributeValueFormat, methodFormat)
  }

  @Provides
  @Singleton
  def provideEdgeFormat(
      attributeFormat: AttributeFormat,
      attributeValueFormat: AttributeValueFormat,
      methodFormat: MethodFormat
  ): EdgeFormat = {
    new EdgeFormat(attributeFormat, attributeValueFormat, methodFormat)
  }

  @Provides
  @Singleton
  def provideAttributeFormat(
      attributeTypeFormat: AttributeTypeFormat,
      attributeValueFormat: AttributeValueFormat
  ): AttributeFormat = {
    new AttributeFormat(attributeTypeFormat, attributeValueFormat)
  }

  @Provides
  @Singleton
  def provideMethodFormat(
      attributeTypeFormat: AttributeTypeFormat
  ): MethodFormat = {
    new MethodFormat(attributeTypeFormat)
  }
}
