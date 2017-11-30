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
import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.ClassFormat
import de.htwg.zeta.common.format.metaModel.DslFormat
import de.htwg.zeta.common.format.metaModel.EnumFormat
import de.htwg.zeta.common.format.metaModel.MetaModelEntityFormat
import de.htwg.zeta.common.format.metaModel.MetaModelFormat
import de.htwg.zeta.common.format.metaModel.MetaModelReleaseFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.format.metaModel.ReferenceFormat
import de.htwg.zeta.common.format.model.EdgeFormat
import de.htwg.zeta.common.format.model.ModelEntityFormat
import de.htwg.zeta.common.format.model.ModelFormat
import de.htwg.zeta.common.format.model.NodeFormat
import net.codingwell.scalaguice.ScalaModule

class JsonFormatModule extends ScalaModule {

  override def configure(): Unit = {
    bind[EventDrivenTaskFormat].toInstance(new EventDrivenTaskFormat)
    bind[BondedTaskFormat].toInstance(new BondedTaskFormat)
    bind[FileFormat].toInstance(new FileFormat)
    bind[DslFormat].toInstance(new DslFormat)
    bind[FilterFormat].toInstance(new FilterFormat)
    bind[GeneratorImageFormat].toInstance(new GeneratorImageFormat(sSchema = s"$$schema", sRef = s"$$ref"))
    bind[GeneratorFormat].toInstance(new GeneratorFormat)
    bind[TimedTaskFormat].toInstance(new TimedTaskFormat)
    bind[AttributeTypeFormat].toInstance(new AttributeTypeFormat)
    bind[AttributeValueFormat].toInstance(new AttributeValueFormat)
    bind[EnumFormat].toInstance(new EnumFormat)
  }

  @Provides @Singleton
  def provideClassFormat(
    attributeFormat: AttributeFormat,
    methodFormat: MethodFormat
  ): ClassFormat = {
    new ClassFormat(attributeFormat, methodFormat)
  }

  @Provides @Singleton
  def provideReferenceFormat(
    attributeFormat: AttributeFormat,
    methodFormat: MethodFormat
  ): ReferenceFormat = {
    new ReferenceFormat(attributeFormat, methodFormat)
  }

  @Provides @Singleton
  def provideMetaModelFormat(
    enumFormat: EnumFormat,
    classFormat: ClassFormat,
    referenceFormat: ReferenceFormat,
    attributeFormat: AttributeFormat,
    methodFormat: MethodFormat
  ): MetaModelFormat = {
    new MetaModelFormat(enumFormat, classFormat, referenceFormat, attributeFormat, methodFormat)
  }

  @Provides @Singleton
  def provideMetaModelEntityFormat(
    metaModelFormat: MetaModelFormat,
    dslFormat: DslFormat
  ): MetaModelEntityFormat = {
    new MetaModelEntityFormat(metaModelFormat, dslFormat)
  }

  @Provides @Singleton
  def provideMetaModelReleaseFormat(
    metaModelFormat: MetaModelFormat,
    dslFormat: DslFormat
  ): MetaModelReleaseFormat = {
    new MetaModelReleaseFormat(metaModelFormat, dslFormat)
  }

  @Provides @Singleton
  def provideModelEntityFormat(
    modelFormat: ModelFormat
  ): ModelEntityFormat = {
    new ModelEntityFormat(modelFormat)
  }

  @Provides @Singleton
  def provideModelFormat(
    nodeFormat: NodeFormat,
    edgeFormat: EdgeFormat,
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat
  ): ModelFormat = {
    new ModelFormat(nodeFormat, edgeFormat, attributeFormat, attributeValueFormat, methodFormat)
  }

  @Provides @Singleton
  def provideNodeFormat(
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat
  ): NodeFormat = {
    new NodeFormat(attributeFormat, attributeValueFormat, methodFormat)
  }

  @Provides @Singleton
  def provideEdgeFormat(
    attributeFormat: AttributeFormat,
    attributeValueFormat: AttributeValueFormat,
    methodFormat: MethodFormat
  ): EdgeFormat = {
    new EdgeFormat(attributeFormat, attributeValueFormat, methodFormat)
  }

  @Provides @Singleton
  def provideAttributeFormat(
    attributeTypeFormat: AttributeTypeFormat,
    attributeValueFormat: AttributeValueFormat
  ): AttributeFormat = {
    new AttributeFormat(attributeTypeFormat, attributeValueFormat)
  }

  @Provides @Singleton
  def provideMethodFormat(
    attributeTypeFormat: AttributeTypeFormat
  ): MethodFormat = {
    new MethodFormat(attributeTypeFormat)
  }

}
