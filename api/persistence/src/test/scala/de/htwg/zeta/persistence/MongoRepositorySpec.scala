package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.mongo.MongoRepository

/**
 * PersistenceMicroServiceTest.
 */
class MongoRepositorySpec extends RepositoryBehavior {

  val repo = new MongoRepository("127.0.0.1:27017", "test")

  "MongoRepository" should behave like repositoryBehavior(repo)

}
