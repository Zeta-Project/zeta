package de.htwg.zeta.server.util

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslInstanceRepository
import javax.inject.Inject
import play.api.libs.json.Writes

case class ExportResult(projectName: String, stream: Source[ByteString, Future[IOResult]])

class ProjectExporter @Inject()(
    modelEntityRepo: AccessRestrictedGraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    gdslProjectRepository: AccessRestrictedGdslProjectRepository,
    gdslProjectFormat: GdslProjectFormat,
    graphicalDslInstanceFormat: GraphicalDslInstanceFormat
) {

  def exportProject(gdslProjectId: UUID, userId: UUID): Future[ExportResult] = {
    val projectRepo = gdslProjectRepository.restrictedTo(userId)
    val instanceRepo = modelEntityRepo.restrictedTo(userId)
    for {
      project <- projectRepo.read(gdslProjectId)
      instanceIds <- instanceRepo.readAllIds()
      allInstances <- Future.sequence(instanceIds.map(instanceRepo.read))
    } yield {
      val instances = allInstances.filter(_.graphicalDslId == project.id)
      val projectAsJson = gdslProjectFormat.writes(project)
      val instancesAsJson = Writes.list(graphicalDslInstanceFormat).writes(instances.toList)
      val files = List(
        File(project.id, "project.json", projectAsJson.toString()),
        File(project.id, "instances.json", instancesAsJson.toString())
      )
      val zipStream = FileZipper.zip(files)
      ExportResult(project.name, zipStream)
    }
  }

}
