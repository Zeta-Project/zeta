package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method.MethodMap

/** The MReference implementation
 *
 * @param name                        the name of the MReference instance
 * @param sourceDeletionDeletesTarget whether source deletion leads to removal of target
 * @param targetDeletionDeletesSource whether target deletion leads to removal of source
 * @param source                      the incoming MClass relationships
 * @param target                      the outgoing MClass relationships
 * @param attributes                  the attributes of the MReference
 */
case class MReference(
    name: String,
    description: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    source: Seq[MClassLinkDef],
    target: Seq[MClassLinkDef],
    attributes: Seq[MAttribute],
    methods: Seq[Method]
) extends MObject with AttributeMap with MethodMap

object MReference {

  def empty(name: String, source: Seq[MClassLinkDef], target: Seq[MClassLinkDef]): MReference =
    MReference(
      name = name,
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      source = source,
      target = target,
      attributes = Seq.empty,
      methods = Seq.empty
    )

  trait ReferenceMap {

    val references: Seq[MReference]

    /** References mapped to their own names. */
    final val referenceMap: Map[String, MReference] = Option(references).fold(
      Map.empty[String, MReference]
    ) { references =>
      references.filter(Option(_).isDefined).map(reference => (reference.name, reference)).toMap
    }

  }

}
