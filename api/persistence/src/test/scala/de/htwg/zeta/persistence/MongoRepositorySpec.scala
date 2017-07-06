package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.behavior.RepositoryBehavior
import de.htwg.zeta.persistence.mongo.MongoRepository

/**
 * MongoRepositorySpec.
 */
class MongoRepositorySpec extends RepositoryBehavior {

  "MongoRepository" should behave like repositoryBehavior(PersistenceMongoDb("test"), restricted = false)

}
