package de.htwg.zeta.persistence.microService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.VersionIndex
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.bondedTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.eventDrivenTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.logFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.settingsFormat
import models.entity
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
import models.file.File


/** Micro-Service Implementation of the PersistenceService.
 *
 * @param address ip-address of the server
 * @param port port of the server
 */
class PersistenceMicroService(address: String, port: Int) extends Repository {

  private implicit val system = ActorSystem("persistenceClient")
  private implicit val materializer = ActorMaterializer()

  /** Persistence for the [[models.entity.EventDrivenTask]] */
  override val eventDrivenTasks: Persistence[EventDrivenTask] = new PersistenceClient[EventDrivenTask](address, port)

  /** Persistence for the [[models.entity.BondedTask]] */
  override val bondTasks: Persistence[BondedTask] = new PersistenceClient[BondedTask](address, port)

  /** Persistence for the [[models.entity.Generator]] */
  override val generators: Persistence[Generator] = new PersistenceClient[Generator](address, port)

  /** Persistence for the [[models.entity.Filter]] */
  override val filters: Persistence[Filter] = new PersistenceClient[Filter](address, port)

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override val generatorImages: Persistence[GeneratorImage] = new PersistenceClient[GeneratorImage](address, port)

  /** Persistence for the [[models.entity.FilterImage]] */
  override val filterImages: Persistence[FilterImage] = new PersistenceClient[FilterImage](address, port)

  /** Persistence for the [[models.entity.Settings]] */
  override val settings: Persistence[Settings] = new PersistenceClient[Settings](address, port)

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override val metaModelEntities: Persistence[MetaModelEntity] = null // TODO

  /** Persistence for the [[models.entity.ModelEntity]] */
  override val modelEntities: Persistence[ModelEntity] = null // TODO

  /** Persistence for the [[models.entity.Log]] */
  override val logs: Persistence[Log] = new PersistenceClient[Log](address, port)

  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: Persistence[AccessAuthorisation] = null

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTasks: Persistence[TimedTask] = null

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices: Persistence[VersionIndex[Int]] = null // TODO

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions: Persistence[EntityVersion[MetaModelRelease]] = null // TODO

  /** Persistence for [[entity.User]] */
  override val users: Persistence[User] = null // TODO

  /** Persistence for the file indices */
  override private[persistence] val fileIndices: Persistence[VersionIndex[String]] = null // TODO

  /** Persistence for the file versions */
  override private[persistence] val fileVersions: Persistence[EntityVersion[File]] = null // TODO

}
