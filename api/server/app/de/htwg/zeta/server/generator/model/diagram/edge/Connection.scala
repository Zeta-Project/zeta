package de.htwg.zeta.server.generator.model.diagram.edge

import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.server.generator.parser.ConnectionSketch
import de.htwg.zeta.server.generator.parser.PropsAndComps
import de.htwg.zeta.server.generator.parser.IDtoConnectionSketch
import models.modelDefinitions.metaModel.elements.MReference

/**
 * Created by julian on 11.12.15.
 * representation of diagram connection
 */
class Connection(corporateStyle: Option[Style], propsAndComps: PropsAndComps, cache: Cache, mc: MReference) {
  val referencedConnection: Option[de.htwg.zeta.server.generator.model.shapecontainer.connection.Connection] = {
    val connectionSketch: ConnectionSketch = IDtoConnectionSketch(propsAndComps.ref)(cache)
    connectionSketch.toConnection(corporateStyle, cache)
  }
  val propertiesAndCompartments = propsAndComps.propertiesAndCompartments
  val vars = propertiesAndCompartments.getOrElse(List()).filter(i => i._1 == "var").map(_._2).map(i =>
    mc.attributes.filter(_.name == i._1).head -> referencedConnection.get.textMap.get(i._2)).toMap
  val vals = propertiesAndCompartments.getOrElse(List()).filter(i => i._1 == "val").map(_._2).map(i =>
    i._1 -> referencedConnection.get.textMap.get(i._2)).toMap
}
