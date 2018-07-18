package de.htwg.zeta.server.util

import java.util.UUID
import java.util.zip.ZipFile

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.project.GdslProjectFormat
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.general.EntityRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers
import org.scalatest.FreeSpec
import scala.concurrent.ExecutionContext.Implicits.global

import org.scalamock.function.FunctionAdapter1
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue

class ProjectImportTest extends FreeSpec with Matchers with MockFactory {

  "A ProjectImporter" - {

    "should import a project" in {

      /*
      val projectId = UUID.randomUUID()
      val instanceId = UUID.randomUUID()
      val userId = UUID.randomUUID()
      val project = new GdslProject(projectId, "project", null, "", "", "")
      val instance = new GraphicalDslInstance(instanceId, "model", projectId, Nil, Nil, Nil, Map.empty, Nil, "")

      // create stub for instance repo
      val modelEntityRepo = stub[AccessRestrictedGraphicalDslInstanceRepository]
      val instanceRepo = stub[EntityRepository[GraphicalDslInstance]]
      (modelEntityRepo.restrictedTo _) when (userId) returns (instanceRepo)
      (instanceRepo.createOrUpdate(_: List[GraphicalDslInstance])) when (List(instance)) returns (Future(List(instance)))


      // create stub for project repo
      val gdslProjectRepository = stub[AccessRestrictedGdslProjectRepository]
      val projectRepo = stub[EntityRepository[GdslProject]]
      (gdslProjectRepository.restrictedTo _) when (userId) returns projectRepo
      (projectRepo.createOrUpdate(_: GdslProject)) when(project) returns(Future(project))

      // stub project format
      val gdslProjectFormat = stub[GdslProjectFormat]
      (gdslProjectFormat.reads _)
        .when(new FunctionAdapter1[JsValue, Boolean](_ => true))
        .returns(JsSuccess(project))

      // stub instance format
      val graphicalDslInstanceFormat = stub[GraphicalDslInstanceFormat]
      (graphicalDslInstanceFormat.reads _)
        .when(new FunctionAdapter1[JsValue, Boolean](_ => true))
        .returns(JsSuccess(instance))


      val projectImporter = new ProjectImporter(modelEntityRepo, gdslProjectRepository, gdslProjectFormat, graphicalDslInstanceFormat)

      val zipFile = new ZipFile("server/test/artifacts/sample-project.zeta")
      val future = projectImporter.importProject(zipFile, userId, "new-project-name")
      val result = Await.result(future, Duration.Inf)
      result shouldBe true
      */
    }

  }

}