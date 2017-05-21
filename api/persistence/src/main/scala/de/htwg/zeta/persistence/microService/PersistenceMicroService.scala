package de.htwg.zeta.persistence.microService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.general.PersistenceService
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.passwordInfoEntityFormat
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
import models.document.PasswordInfoEntity
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
  override val eventDrivenTask: Persistence[EventDrivenTask] = null

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTask: Persistence[BondedTask] = null

  /** Persistence for the [[models.document.Generator]] */
  override val generator: Persistence[Generator] = null

  /** Persistence for the [[models.document.Filter]] */
  override val filter: Persistence[Filter] = null

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImage: Persistence[GeneratorImage] = null

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImage: Persistence[FilterImage] = null

  /** Persistence for the [[models.document.Settings]] */
  override val settings: Persistence[Settings] = null

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntity: Persistence[MetaModelEntity] = null

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override val metaModelRelease: Persistence[MetaModelRelease] = null

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntity: Persistence[ModelEntity] = null

  /** Persistence for the [[models.document.Log]] */
  override val log: Persistence[Log] = null

  /** Persistence for the [[models.document.PasswordInfoEntity]] */
  override val passwordInfoEntity = new PersistenceClient[PasswordInfoEntity](address, port)

  /** Persistence for the [[models.document.UserEntity]] */
  override val userEntity = new PersistenceClient[UserEntity](address, port)

}
