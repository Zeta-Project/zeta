package de.htwg.zeta.persistence

import scala.concurrent.Future

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.mongo.MongoAccessAuthorisationRepository
import de.htwg.zeta.persistence.mongo.MongoBondedTaskRepository
import de.htwg.zeta.persistence.mongo.MongoEventDrivenTaskRepository
import de.htwg.zeta.persistence.mongo.MongoFileRepository
import de.htwg.zeta.persistence.mongo.MongoFilterImageRepository
import de.htwg.zeta.persistence.mongo.MongoFilterRepository
import de.htwg.zeta.persistence.mongo.MongoGeneratorImageRepository
import de.htwg.zeta.persistence.mongo.MongoGeneratorRepository
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository
import de.htwg.zeta.persistence.mongo.MongoLogRepository
import de.htwg.zeta.persistence.mongo.MongoGraphicalDslRepository
import de.htwg.zeta.persistence.mongo.MongoGraphicalDslReleaseRepository
import de.htwg.zeta.persistence.mongo.MongoGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository
import de.htwg.zeta.persistence.mongo.MongoSettingsRepository
import de.htwg.zeta.persistence.mongo.MongoTimedTaskRepository
import de.htwg.zeta.persistence.mongo.MongoUserRepository
import org.scalatest.Ignore
import reactivemongo.api.MongoDriver

/**
 * MongoRepositorySpec.
 */
@Ignore
class MongoRepositorySpec extends RepositoryBehavior {

  private val connection = Future.fromTry(MongoDriver().connection("localhost")).flatMap(_.database("zeta-test"))

  "MongoRepository" should behave like repositoryBehavior(
    new MongoAccessAuthorisationRepository(connection),
    new MongoBondedTaskRepository(connection),
    new MongoEventDrivenTaskRepository(connection),
    new MongoFilterRepository(connection),
    new MongoFilterImageRepository(connection),
    new MongoGeneratorRepository(connection),
    new MongoGeneratorImageRepository(connection),
    new MongoLogRepository(connection),
    new MongoGraphicalDslRepository(connection),
    new MongoGraphicalDslReleaseRepository(connection),
    new MongoGraphicalDslInstanceRepository(connection),
    new MongoSettingsRepository(connection),
    new MongoTimedTaskRepository(connection),
    new MongoUserRepository(connection),
    new MongoFileRepository(connection),
    new MongoLoginInfoRepository(connection),
    new MongoPasswordInfoRepository(connection)
  )

}
