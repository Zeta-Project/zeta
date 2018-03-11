package de.htwg.zeta.server.generator.model.diagram.methodes

import de.htwg.zeta.common.models.project.concept.elements.MAttribute


/**
 *  ???
 * @param actionBlock ??
 * @param askFor ??
 */
case class OnCreate(
    override val actionBlock: ActionBlock,
    askFor: MAttribute
) extends Methode

