package de.htwg.zeta.server.generator.generators.diagram

import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.edge.Edge

import scala.util.Success
import scala.util.Try

/**
 * Created by julian on 10.02.16.
 */
object LinkhelperGenerator {
  def generate(diagram: Diagram) = {
    s"""
    $generateHead
    ${generateLinkhelper(diagram)}
    """
  }

  def generateHead =
    """
    /*
     * This is a generated linkhelper file for JointJS
     */
    """

  protected def generateLinkhelper(diagram: Diagram) =
    s"""
    var linkhelper = {
      ${generatePlacingTexts(diagram)}
      $generateHelperMethods
      ${generateMapping(diagram.edges)}
    };
    """

  protected def generatePlacingTexts(diagram: Diagram) = {
    s"""
    placingTexts:{
      ${diagram.edges.map(e => s"""${e.name}: { ${generateStringProperties(e).mkString(",")} }""").mkString(",")}
    },
    """
  }

  protected def generateStringProperties(edge: Edge) = {
    val stringProperties = edge.connection.vals
    for {(key, value) <- stringProperties} yield s"""'$key': "${value.getOrElse("").toString}""""
  }

  protected def generateHelperMethods =
    """
    getLabelText: function(edge, textId){
      var text = this.placingTexts[edge][textId];
      if(text === undefined){
        text = "";
      }
      return text;
    },
    """

  private def generateMapping(edges: Iterable[Edge]) =
    s"""
      mapping: {
        ${edges.map(generateMappingEntry).mkString(",")}
      }
    """

  private def generateMappingEntry(edge: Edge) =
  s"""
    ${edge.mcoreElement.name}: {
      ${edge.mcoreElement.attributes.map(attribute => generateMappingEntryAttribute(edge, attribute.name)).mkString(",")}
    }
  """

  private def generateMappingEntryAttribute(edge: Edge, name: String): String = {
    Try(edge.connection.vars.filter{case (attribute, _) => attribute.name == name}.head) match {
      case Success(i) => i._2 match {
        case Some(text) => s"""${text.id}: "$name" """
        case _ => ""
      }
      case _ => ""
    }
  }
}
