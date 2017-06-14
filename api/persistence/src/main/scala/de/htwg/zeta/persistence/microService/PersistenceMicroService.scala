package de.htwg.zeta.persistence.microService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
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
  override val eventDrivenTask: EntityPersistence[EventDrivenTask] = new PersistenceClient[EventDrivenTask](address, port)

  /** Persistence for the [[models.entity.BondedTask]] */
  override val bondedTask: EntityPersistence[BondedTask] = new PersistenceClient[BondedTask](address, port)

  /** Persistence for the [[models.entity.Generator]] */
  override val generator: EntityPersistence[Generator] = new PersistenceClient[Generator](address, port)

  /** Persistence for the [[models.entity.Filter]] */
  override val filter: EntityPersistence[Filter] = new PersistenceClient[Filter](address, port)

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override val generatorImage: EntityPersistence[GeneratorImage] = new PersistenceClient[GeneratorImage](address, port)

  /** Persistence for the [[models.entity.FilterImage]] */
  override val filterImage: EntityPersistence[FilterImage] = new PersistenceClient[FilterImage](address, port)

  /** Persistence for the [[models.entity.Settings]] */
  override val settings: EntityPersistence[Settings] = new PersistenceClient[Settings](address, port)

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override val metaModelEntity: EntityPersistence[MetaModelEntity] = null // TODO

  /** Persistence for the [[models.entity.ModelEntity]] */
  override val modelEntity: EntityPersistence[ModelEntity] = null // TODO

  /** Persistence for the [[models.entity.Log]] */
  override val log: EntityPersistence[Log] = new PersistenceClient[Log](address, port)

  /** Persistence for AccessAuthorisation */
  override val accessAuthorisation: EntityPersistence[AccessAuthorisation] = null

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTask: EntityPersistence[TimedTask] = null

  /** Persistence for [[entity.User]] */
  override val user: EntityPersistence[User] = null // TODO

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override val metaModelRelease: EntityPersistence[MetaModelRelease] = null // TODO

  /** Versioned Persistence for [[entity.File]] */
  override val file: FilePersistence = null // TODO

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo: LoginInfoPersistence = null // TODO

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo: PasswordInfoPersistence = null // TODO

}
