package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.mongo.MongoRepository

/**
 * PersistenceMicroServiceTest.
 */
class MongoRepositorySpec extends RepositoryBehavior {

  "MongoRepository" should behave like repositoryBehavior(new MongoRepository("127.0.0.1:27017", "test"), restricted = false)

}
