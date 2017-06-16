package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.mongo.MongoRepository

/**
 * MongoRepositorySpec.
 */
class MongoRepositorySpec extends RepositoryBehavior {

  "MongoRepository" should behave like repositoryBehavior(new MongoRepository("localhost:27017", "test"), restricted = false)

}
