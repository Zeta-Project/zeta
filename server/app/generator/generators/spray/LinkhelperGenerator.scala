package generator.generators.spray

import generator.model.diagram.Diagram

/**
 * Created by julian on 10.02.16.
 */
object LinkhelperGenerator {
  def generate(diagram:Diagram)={
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

  protected def generateLinkhelper(diagram:Diagram)=
    s"""
    var linkhelper = {
      ${generatePlacingTexts(diagram)}
      $generateHelperMethods
    };
    """

  /*TODO wath over this method, probably misunderstood something... compare to LinkhelperGenerator.xtext of MoDiGen_V2!*/
  protected def generatePlacingTexts(diagram:Diagram)={
    s"""
    placingTexts:{
      ${for(e <- diagram.edges) yield
      s"""${e.name}: {
      ${val stringProperties = e.connection.vals //TODO only vals?
      for(((key, value), i) <- stringProperties.zipWithIndex) yield
      s"""'$key': "${value.toString /* TODO value.toString.getValue()*/}"${if(i != stringProperties.size)","}"""}
    }${if(e != diagram.edges.last)","}"""
      }
    },

    """
  }

  protected def generateHelperMethods =
    """
    getLabelText: function(edge, textId){
      var text = this.placingTexts[edge][textId];
      if(text === undefined){
        text = "";
      }

      return text;
    }
    """
}
