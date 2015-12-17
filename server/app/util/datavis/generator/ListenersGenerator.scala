package util.datavis.generator

import java.io.{File, FileWriter}

import util.datavis.domain._

class ListenersGenerator {
  val filesuffix = "listeners.js"
  val public = "server" + File.separator + "public" + File.separator
  val path = "javascripts" + File.separator + "generated" + File.separator

  def generate(diagramId:String, objectId:String, conditionals:List[Conditional]) = {
    val content = generateContents(objectId, conditionals).stripMargin(' ')
    mkDir(diagramId)
    val fileName = path + diagramId + File.separator + objectId + filesuffix
    val writer = new FileWriter(public + fileName)
    writer.append(content)
    writer.flush()
    writer.close()
    fileName
  }
  
  def generateContents(objectId: String, conditionals:List[Conditional]) = generateHead + generateListenerObject(objectId) + generateListeners(objectId, conditionals)

  def generateHead =
    """|/*
      |* Individual listeners for model.
      |*/
    """.stripMargin

  def generateListenerObject(objectId:String) =
  s"""
     | dataVisListeners["$objectId"] = {};
     | _.extend(dataVisListeners["$objectId"], Backbone.Events);
   """.stripMargin

  def generateListeners(objectId:String, conditional: List[Conditional]) = conditional.foldLeft("")((listeners, next) => listeners + generateListener(objectId, next))

  def generateListener(objectId:String, conditional: Conditional) = {
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

  def generateComparisonOperand(operand:Operand) = operand match{
    case lit:Literal => lit.toString
    case attr:MIdentifier => "cell.get('mAttributes')." + attr.toString
    case style:StyleIdentifier => "attributes.attrs[\'" + style.selector + "\'][\'" + style.identifier + "\']"
  }

  def generateAssignment(assignment: Assignment) = assignment.target match {
    case attr:MIdentifier => attr.toString + " = " + assignment.value + ";"
    case style:StyleIdentifier => "cell.attr(\"" + style.selector + "/" + style.identifier + "\", " + assignment.value + ");"
  }

  private def mkDir(diagramId:String) = new File(public + path + diagramId).mkdirs()
}
