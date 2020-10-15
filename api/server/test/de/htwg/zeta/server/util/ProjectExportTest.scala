package de.htwg.zeta.server.util

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.{EntityRepository, GraphicalDslInstanceRepository}
import org.scalatest.matchers.should.Matchers
import de.htwg.zeta.common.models.project.concept.Concept
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.JsObject
import org.mockito.MockitoSugar

class ProjectExportTest extends AnyFreeSpec with Matchers with MockitoSugar {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  "A ProjectExporter" - {

    "should export a project" in {

      val projectId = UUID.randomUUID()
      val instanceId = UUID.randomUUID()
      val userId = UUID.randomUUID()
      val project = new GdslProject(projectId, "project", Concept.empty, "", "", "")
      val instance = new GraphicalDslInstance(instanceId, "model", projectId, Nil, Nil, Nil, Map.empty, Nil, "")

      // create stub for instance repo
      val instanceRepo = mock[GraphicalDslInstanceRepository]
      when(instanceRepo.readAllIds()) thenReturn Future(Set(instanceId))
      when(instanceRepo.read(instanceId)) thenReturn Future(instance)

      // create stub for project repo
      val gdslProjectRepository = mock[AccessRestrictedGdslProjectRepository]
      val projectRepo = mock[EntityRepository[GdslProject]]
      when(gdslProjectRepository.restrictedTo(userId)) thenReturn projectRepo
      when(projectRepo.readAllIds()) thenReturn Future(Set(projectId))
      when(projectRepo.read(projectId)) thenReturn Future(project)

      // stub instance format
      val graphicalDslInstanceFormat = mock[GraphicalDslInstanceFormat]
      when(graphicalDslInstanceFormat.writes(instance)) thenReturn JsObject.empty

      // stub project format
      val gdslProjectFormat = mock[GdslProjectFormat]
      when(gdslProjectFormat.writes(project)) thenReturn JsObject.empty

      // test
      val projectExporter = new ProjectExporter(instanceRepo, gdslProjectRepository, gdslProjectFormat, graphicalDslInstanceFormat)
      val future = projectExporter.exportProject(projectId, userId)
      val result = Await.result(future, Duration.Inf)
      result.projectName shouldBe project.name
    }
  }
}
