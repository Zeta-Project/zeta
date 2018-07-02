package de.htwg.zeta.server.util

import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.zip.ZipFile
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import scala.util.Success
import scala.util.Failure

import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import org.apache.commons.io.IOUtils
import play.api.libs.json._


class ProjectImporter @Inject() (
    modelEntityRepo: AccessRestrictedGraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGdslProjectRepository,
    gdslProjectRepository: AccessRestrictedGdslProjectRepository,
    gdslProjectFormat: GdslProjectFormat,
    graphicalDslInstanceFormat: GraphicalDslInstanceFormat,
) {

  def importProject(zipFile: ZipFile, userId: UUID): Future[Boolean] = {
    val metaModelRepo = metaModelEntityRepo.restrictedTo(userId)
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
          for {
            _ <- metaModelRepo.createOrUpdate(project.get)
            _ <- modelRepo.createOrUpdate(instances.get)
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
