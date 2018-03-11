package de.htwg.zeta.common.models.modelDefinitions.concept.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.Method.MethodMap

/** The MReference implementation
 *
 * @param name                        the name of the MReference instance
 * @param sourceDeletionDeletesTarget whether source deletion leads to removal of target
 * @param targetDeletionDeletesSource whether target deletion leads to removal of source
 * @param sourceClassName             the name of the incoming MClass relationship
 * @param targetClassName             the name of the outgoing MClass relationship
 * @param attributes                  the attributes of the MReference
 */
case class MReference(
    name: String,
    description: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    sourceClassName: String,
    targetClassName: String,
    attributes: Seq[MAttribute],
    methods: Seq[Method]
) extends AttributeMap with MethodMap

object MReference {

  def empty(name: String, source: String, target: String): MReference =
    MReference(
      name = name,
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      sourceClassName = source,
      targetClassName = target,
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
