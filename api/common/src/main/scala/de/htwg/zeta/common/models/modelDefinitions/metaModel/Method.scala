package de.htwg.zeta.common.models.modelDefinitions.metaModel

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MethodParameter
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MethodDeclaration


object Method extends App {

  val methods: Map[MethodDeclaration, String] = Map(

    MethodDeclaration("foo", Seq.empty) ->
      """
       |print("Test")
      """.stripMargin,

    MethodDeclaration("add", Seq(MethodParameter("n", IntType))) ->
      """
       |counter += n
      """.stripMargin

  )

}
