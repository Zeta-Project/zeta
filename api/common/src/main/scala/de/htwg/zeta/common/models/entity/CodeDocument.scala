package de.htwg.zeta.common.models.entity

import java.util.UUID

import scalot.Server

/** */

/** Represents a Server-side CodeDocument which is stored in the Database, used by the ace-web-editor.
 *
 * @param id             The id of the CodeDocument
 * @param dslType        The type of the dsl
 * @param metaModelId    The id of the MetaModel
 * @param serverDocument The server document instance
 */
case class CodeDocument(
    id: UUID,
    dslType: String,
    metaModelId: UUID,
    serverDocument: Server
) extends Entity
