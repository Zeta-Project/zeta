package de.htwg.zeta.server.util

import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.zip.ZipFile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslInstanceRepository
import javax.inject.Inject
import org.apache.commons.io.IOUtils
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Reads


class ProjectImporter @Inject()(
    modelEntityRepo: AccessRestrictedGraphicalDslInstanceRepository,
    gdslProjectRepository: AccessRestrictedGdslProjectRepository,
    gdslProjectFormat: GdslProjectFormat,
    graphicalDslInstanceFormat: GraphicalDslInstanceFormat
) {

  def importProject(zipFile: ZipFile, userId: UUID, newProjectName: String): Future[Boolean] = {
    val metaModelRepo = gdslProjectRepository.restrictedTo(userId)
    val modelRepo = modelEntityRepo.restrictedTo(userId)

    val maybeProject =
      unzip(zipFile, "project.json")
        .map(Json.parse)
        .map(gdslProjectFormat.reads)

    val maybeInstances =
      unzip(zipFile, "instances.json")
        .map(Json.parse)
        .map(Reads.list(graphicalDslInstanceFormat).reads)

    if (maybeProject.isFailure || maybeInstances.isFailure) {
      Future(false)
    } else {
      (maybeProject.get, maybeInstances.get) match {
        case (project: JsSuccess[GdslProject], instances: JsSuccess[List[GraphicalDslInstance]]) =>
          val projectCopy = project.get.copy(id = UUID.randomUUID(), name = newProjectName)
          val instanceCopies = instances.get.map(i => i.copy(id = UUID.randomUUID(), graphicalDslId = projectCopy.id))
          for {
            _ <- metaModelRepo.createOrUpdate(projectCopy)
            _ <- modelRepo.createOrUpdate(instanceCopies)
          } yield {
            true
          }
        case _ =>
          Future(false)
      }
    }
  }

  private def unzip(zipFile: ZipFile, file: String): Try[String] = {
    val out = new ByteArrayOutputStream()
    Try {
      val zipEntry = zipFile.getEntry(file)
      val in = zipFile.getInputStream(zipEntry)
      IOUtils.copy(in, out)
      val content = out.toByteArray.map(_.toChar).mkString
      content
    }
  }

}
