package de.htwg.zeta.persistence.microService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.bondedTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.eventDrivenTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.logFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.settingsFormat
import models.entity
import models.entity.AccessAuthorisation
import models.entity.BondedTask
import models.entity.EventDrivenTask
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


/** Micro-Service Implementation of the PersistenceService.
 *
 * @param address ip-address of the server
 * @param port port of the server
 */
class PersistenceMicroService(address: String, port: Int) extends Repository {

  private implicit val system = ActorSystem("persistenceClient")
  private implicit val materializer = ActorMaterializer()

  /** Persistence for the [[models.entity.EventDrivenTask]] */
  override val eventDrivenTasks: EntityPersistence[EventDrivenTask] = new PersistenceClient[EventDrivenTask](address, port)

  /** Persistence for the [[models.entity.BondedTask]] */
  override val bondTasks: EntityPersistence[BondedTask] = new PersistenceClient[BondedTask](address, port)

  /** Persistence for the [[models.entity.Generator]] */
  override val generators: EntityPersistence[Generator] = new PersistenceClient[Generator](address, port)

  /** Persistence for the [[models.entity.Filter]] */
  override val filters: EntityPersistence[Filter] = new PersistenceClient[Filter](address, port)

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override val generatorImages: EntityPersistence[GeneratorImage] = new PersistenceClient[GeneratorImage](address, port)

  /** Persistence for the [[models.entity.FilterImage]] */
  override val filterImages: EntityPersistence[FilterImage] = new PersistenceClient[FilterImage](address, port)

  /** Persistence for the [[models.entity.Settings]] */
  override val settings: EntityPersistence[Settings] = new PersistenceClient[Settings](address, port)

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override val metaModelEntities: EntityPersistence[MetaModelEntity] = null // TODO

  /** Persistence for the [[models.entity.ModelEntity]] */
  override val modelEntities: EntityPersistence[ModelEntity] = null // TODO

  /** Persistence for the [[models.entity.Log]] */
  override val logs: EntityPersistence[Log] = new PersistenceClient[Log](address, port)

  /** Persistence for AccessAuthorisation */
  override val accessAuthorisations: EntityPersistence[AccessAuthorisation] = null

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTasks: EntityPersistence[TimedTask] = null

  /** Persistence for [[entity.User]] */
  override val users: EntityPersistence[User] = null // TODO

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override val metaModelReleases: EntityPersistence[MetaModelRelease] = null // TODO

  /** Versioned Persistence for [[entity.File]] */
  override val files: FilePersistence = null // TODO
}
