package de.htwg.zeta.server.util

import java.util.UUID

import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.Future

import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.general.{EntityRepository, GraphicalDslInstanceRepository}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

import de.htwg.zeta.common.models.project.concept.Concept
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.JsObject

class ProjectExportTest extends AnyFreeSpec with Matchers with MockFactory {

  "A ProjectExporter" - {

    "should export a project" in {

      val projectId = UUID.randomUUID()
      val instanceId = UUID.randomUUID()
      val userId = UUID.randomUUID()
      val project = new GdslProject(projectId, "project", Concept.empty, "", "", "")
      val instance = new GraphicalDslInstance(instanceId, "model", projectId, Nil, Nil, Nil, Map.empty, Nil, "")

      // create stub for instance repo
      val instanceRepo = stub[GraphicalDslInstanceRepository]
      (instanceRepo.readAllIds _) when() returns (Future(Set(instanceId)))
      (instanceRepo.read _) when (instanceId) returns (Future(instance))

      // create stub for project repo
      val gdslProjectRepository = stub[AccessRestrictedGdslProjectRepository]
      val projectRepo = stub[EntityRepository[GdslProject]]
      (gdslProjectRepository.restrictedTo _) when (userId) returns projectRepo
      (projectRepo.readAllIds _) when() returns (Future(Set(projectId)))
      (projectRepo.read _) when (projectId) returns (Future(project))

      // stub project format
      val gdslProjectFormat = stub[GdslProjectFormat]
      (gdslProjectFormat.writes _) when (project) returns (JsObject.empty)

      // stub instance format
      val graphicalDslInstanceFormat = stub[GraphicalDslInstanceFormat]
      (graphicalDslInstanceFormat.writes _) when (instance) returns (JsObject.empty)

      // test
      val projectExporter = new ProjectExporter(instanceRepo, gdslProjectRepository, gdslProjectFormat, graphicalDslInstanceFormat)
      val future = projectExporter.exportProject(projectId, userId)
      val result = Await.result(future, Duration.Inf)
      result.projectName shouldBe project.name
    }
  }
}
