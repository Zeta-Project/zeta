package de.htwg.zeta.persistence.microService

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.VersionIndex
import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.bondedTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.eventDrivenTaskFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.filterImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.generatorImageFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.logFormat
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.settingsFormat
import models.User
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
import models.document.TimedTask
import models.file.File


/** Micro-Service Implementation of the PersistenceService.
 *
 * @param address ip-address of the server
 * @param port port of the server
 */
class PersistenceMicroService(address: String, port: Int) extends Repository {

  private implicit val system = ActorSystem("persistenceClient")
  private implicit val materializer = ActorMaterializer()

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override val eventDrivenTasks: Persistence[EventDrivenTask] = new PersistenceClient[EventDrivenTask](address, port)

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTasks: Persistence[BondedTask] = new PersistenceClient[BondedTask](address, port)

  /** Persistence for the [[models.document.Generator]] */
  override val generators: Persistence[Generator] = new PersistenceClient[Generator](address, port)

  /** Persistence for the [[models.document.Filter]] */
  override val filters: Persistence[Filter] = new PersistenceClient[Filter](address, port)

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImages: Persistence[GeneratorImage] = new PersistenceClient[GeneratorImage](address, port)

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImages: Persistence[FilterImage] = new PersistenceClient[FilterImage](address, port)

  /** Persistence for the [[models.document.Settings]] */
  override val settings: Persistence[Settings] = new PersistenceClient[Settings](address, port)

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntities: Persistence[MetaModelEntity] = null // TODO

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntities: Persistence[ModelEntity] = null // TODO

  /** Persistence for the [[models.document.Log]] */
  override val logs: Persistence[Log] = new PersistenceClient[Log](address, port)

  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: Persistence[AccessAuthorisation] = null

  /** Persistence for [[models.document.TimedTask]] */
  override val timedTasks: Persistence[TimedTask] = null

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices: Persistence[VersionIndex[Int]] = null // TODO

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions: Persistence[EntityVersion[MetaModelRelease]] = null // TODO

  /** Persistence for [[models.User]] */
  override val users: Persistence[User] = null // TODO

  /** Persistence for the file indices */
  override private[persistence] val fileIndices: Persistence[VersionIndex[String]] = null // TODO

  /** Persistence for the file versions */
  override private[persistence] val fileVersions: Persistence[EntityVersion[File]] = null // TODO

}
