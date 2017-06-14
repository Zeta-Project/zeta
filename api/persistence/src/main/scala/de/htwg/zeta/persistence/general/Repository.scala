package de.htwg.zeta.persistence.general

import models.entity
import models.entity.AccessAuthorisation
import models.entity.BondedTask
import models.entity.EventDrivenTask
import models.entity.File
import models.entity.Filter
import models.entity.FilterImage
import models.entity.Generator
import models.entity.GeneratorImage
import models.entity.Log
import models.entity.MetaModelEntity
import models.entity.MetaModelRelease
import models.entity.ModelEntity
import models.entity.Settings
import models.entity.TimedTask
import models.entity.User

/** Persistence Implementation for the different types of entities. */
trait Repository {

  /** Persistence for AccessAuthorisation */
  def accessAuthorisations: EntityPersistence[AccessAuthorisation]

  /** Persistence for [[models.entity.EventDrivenTask]] */
  def eventDrivenTasks: EntityPersistence[EventDrivenTask]

  /** Persistence for [[models.entity.BondedTask]] */
  def bondTasks: EntityPersistence[BondedTask]

  /** Persistence for [[models.entity.TimedTask]] */
  def timedTasks: EntityPersistence[TimedTask]

  /** Persistence for [[models.entity.Generator]] */
  def generators: EntityPersistence[Generator]

  /** Persistence for [[models.entity.Filter]] */
  def filters: EntityPersistence[Filter]

  /** Persistence for [[models.entity.GeneratorImage]] */
  def generatorImages: EntityPersistence[GeneratorImage]

  /** Persistence for [[models.entity.FilterImage]] */
  def filterImages: EntityPersistence[FilterImage]

  /** Persistence for [[models.entity.Settings]] */
  def settings: EntityPersistence[Settings]

  /** Persistence for [[models.entity.MetaModelEntity]] */
  def metaModelEntities: EntityPersistence[MetaModelEntity]

  /** Persistence for [[models.entity.MetaModelRelease]] */
  def metaModelReleases: EntityPersistence[MetaModelRelease]

  /** Persistence for [[models.entity.ModelEntity]] */
  def modelEntities: EntityPersistence[ModelEntity]

  /** Persistence for [[models.entity.Log]] */
  def logs: EntityPersistence[Log]

  /** Persistence for [[entity.User]] */
  def users: EntityPersistence[User]

  /** Versioned Persistence for [[File]] */
  def files: FilePersistence

}
