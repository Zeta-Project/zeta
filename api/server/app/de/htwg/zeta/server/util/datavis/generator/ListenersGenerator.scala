package de.htwg.zeta.server.util.datavis.generator

import java.io.File
import java.io.FileWriter
import java.util.UUID

import de.htwg.zeta.server.util.datavis.domain.Assignment
import de.htwg.zeta.server.util.datavis.domain.Conditional
import de.htwg.zeta.server.util.datavis.domain.Literal
import de.htwg.zeta.server.util.datavis.domain.MIdentifier
import de.htwg.zeta.server.util.datavis.domain.Operand
import de.htwg.zeta.server.util.datavis.domain.StyleIdentifier

class ListenersGenerator {
  val filesuffix = "listeners.js"
  val public = "server" + File.separator + "public" + File.separator
  val path = "javascripts" + File.separator + "generated" + File.separator

  def generate(diagramId: UUID, objectId: UUID, conditionals: List[Conditional]) = {
    val content = generateContents(objectId, conditionals).stripMargin(' ')
    mkDir(diagramId)
    val fileName = path + diagramId + File.separator + objectId + filesuffix
    val writer = new FileWriter(public + fileName)
    writer.append(content)
    writer.flush()
    writer.close()
    fileName
  }

  def generateContents(objectId: UUID, conditionals: List[Conditional]) = {
    generateHead + generateListenerObject(objectId) + generateListeners(objectId, conditionals)
  }

  def generateHead =
    """
      |/*
      | * Individual listeners for model.
      | */
    """.stripMargin

  def generateListenerObject(objectId: UUID) =
    s"""
      | dataVisListeners["$objectId"] = {};
      | _.extend(dataVisListeners["$objectId"], Backbone.Events);
    """.stripMargin

  def generateListeners(objectId: UUID, conditional: List[Conditional]) = {
    conditional.foldLeft("")((listeners, next) => listeners + generateListener(objectId, next))
  }

  def generateListener(objectId: UUID, conditional: Conditional) = {
    val comparisonLeft = generateComparisonOperand(conditional.condition.x)
    val comparisonRight = generateComparisonOperand(conditional.condition.y)
    val comparison = conditional.condition.comparison
    val assignment = generateAssignment(conditional.assignment)
    val tmp = conditional.assignment.target
    s"""
      |dataVisListeners["$objectId"].listenTo(window.globalGraph.getCell("$objectId"),'change:mAttributes', function(){
      | var cell = window.globalGraph.getCell("$objectId");
      | if($comparisonLeft $comparison $comparisonRight){
      |   $assignment
      | }
      |});
    """.stripMargin
  }

  def generateComparisonOperand(operand: Operand) = operand match {
    case lit: Literal => lit.toString
    case attr: MIdentifier => "cell.get('mAttributes')." + attr.toString
    case style: StyleIdentifier => "attributes.attrs[\'" + style.selector + "\'][\'" + style.identifier + "\']"
  }

  def generateAssignment(assignment: Assignment) = assignment.target match {
    case attr: MIdentifier => attr.toString + " = " + assignment.value + ";"
    case style: StyleIdentifier => "cell.attr(\"" + style.selector + "/" + style.identifier + "\", " + assignment.value + ");"
  }

  private def mkDir(diagramId: UUID) = new File(public + path + diagramId.toString).mkdirs()
}
