package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.Json
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


object MetaModelFormat {

  private val sName = "name"

  val playJsonReads: Reads[MetaModel] = Reads{json =>
    for {
      name <- (json \ sName).validate[String]
      enums <- (json \ "enums").validate(Reads.list(MEnumFormat))
      classes <- (json \ "classes").validate(Reads.list(MClass.playJsonReads(enums)))
      references <- (json \ "references").validate(Reads.list(MReference.playJsonReads(enums)))
      attributes <- (json \ "attributes").validate(Reads.list(MAttribute.playJsonReads(enums)))
      methods <- (json \ "methods").validate(Reads.list(Method.playJsonReads(enums)))
      uiState <- (json \ "uiState").validate[String]
    } yield {
      MetaModel(
        name = name,
        classes = classes,
        references = references,
        enums = enums,
        attributes = attributes,
        methods = methods,
        uiState = uiState
      )
    }
  }

  val playJsonReadsEmpty: Reads[MetaModel] = Reads { json =>
    (json \ sName).validate[String].map(MetaModel.empty)
  }

  val playJsonWrites: OWrites[MetaModel] = Json.writes[MetaModel]

}
