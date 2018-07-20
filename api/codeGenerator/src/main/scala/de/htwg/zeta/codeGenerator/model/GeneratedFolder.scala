package de.htwg.zeta.codeGenerator.model

case class GeneratedFolder(
    name: String,
    files: List[GeneratedFile],
    children: List[GeneratedFolder]
)
