package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.mongo.MongoRepository

/**
 * PersistenceMicroServiceTest.
 */
class MongoRepositorySpec extends RepositoryBehavior {

  "MongoRepositorySpec" should behave like serviceBehavior(new MongoRepository("127.0.0.1:27017", "test"))

}
