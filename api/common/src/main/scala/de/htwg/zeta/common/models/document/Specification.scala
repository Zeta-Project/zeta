package de.htwg.zeta.common.models.document

trait Specification {
  val http: HttpSpecification
}

sealed trait HttpSpecification

/**
 * Query all docs in a specified range
 */
case class HttpAllDocsQuery(startkey: String, endkey: String, limit: String = "10") extends HttpSpecification

case class AllMetaModels() extends Specification {
  val http = HttpAllDocsQuery(startkey = "MetaModelEntity-0", endkey = "MetaModelEntity-\uffff")
}

case class AllModels() extends Specification {
  val http = HttpAllDocsQuery(startkey = "ModelEntity-0", endkey = "ModelEntity-\uffff")
}

case class AllMetaModelReleases(from: MetaModelEntity) extends Specification {
  private val firstRelease = from._id.replace("MetaModelEntity", "MetaModelRelease")
  private val lastRelease = from._id.replace("MetaModelEntity", "MetaModelRelease")

  val http = HttpAllDocsQuery(startkey = s"${firstRelease}-0", endkey = s"${lastRelease}-\uffff")
}

case class AllUsers() extends Specification {
  val http = HttpAllDocsQuery(startkey = "UserEntity-0", endkey = "UserEntity-\uffff")
}

case class AllFilters() extends Specification {
  val http = HttpAllDocsQuery(startkey = "Filter-0", endkey = "Filter-\uffff")
}

case class AllEventDrivenTasks() extends Specification {
  val http = HttpAllDocsQuery(startkey = "EventDrivenTask-0", endkey = "EventDrivenTask-\uffff")
}

case class AllBondedTasks() extends Specification {
  val http = HttpAllDocsQuery(startkey = "BondedTask-0", endkey = "BondedTask-\uffff")
}
