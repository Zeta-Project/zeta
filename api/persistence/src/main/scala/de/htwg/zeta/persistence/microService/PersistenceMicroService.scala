package de.htwg.zeta.persistence.microService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.PersistenceService
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.bondedTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.eventDrivenTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.logFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.passwordInfoEntityFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.settingsFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.userEntityFormat
import models.document.BondedTask
import models.document.EventDrivenTask
import models.document.Filter
import models.document.FilterImage
import models.document.Generator
import models.document.GeneratorImage
import models.document.Log
import models.document.MetaModelEntity
import models.document.MetaModelRelease
import models.document.ModelEntity
import models.document.Settings
import models.document.UserEntity


/** Micro-Service Implementation of the PersistenceService.
 *
 * @param address ip-address of the server
 * @param port port of the server
 */
class PersistenceMicroService(address: String, port: Int) extends PersistenceService {

  private implicit val system = ActorSystem("persistenceClient")
  private implicit val materializer = ActorMaterializer()

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override val eventDrivenTask: Persistence[EventDrivenTask] = new PersistenceClient[EventDrivenTask](address, port)

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTask: Persistence[BondedTask] = new PersistenceClient[BondedTask](address, port)

  /** Persistence for the [[models.document.Generator]] */
  override val generator: Persistence[Generator] = new PersistenceClient[Generator](address, port)

  /** Persistence for the [[models.document.Filter]] */
  override val filter: Persistence[Filter] = new PersistenceClient[Filter](address, port)

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImage: Persistence[GeneratorImage] = new PersistenceClient[GeneratorImage](address, port)

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImage: Persistence[FilterImage] = new PersistenceClient[FilterImage](address, port)

  /** Persistence for the [[models.document.Settings]] */
  override val settings: Persistence[Settings] = new PersistenceClient[Settings](address, port)

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntity: Persistence[MetaModelEntity] = null // TODO

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override val metaModelRelease: Persistence[MetaModelRelease] = null // TODO

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntity: Persistence[ModelEntity] = null // TODO

  /** Persistence for the [[models.document.Log]] */
  override val log: Persistence[Log] = new PersistenceClient[Log](address, port)

  /** Persistence for the [[models.document.UserEntity]] */
  override val userEntity = new PersistenceClient[UserEntity](address, port)

}
