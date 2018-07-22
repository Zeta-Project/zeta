package de.htwg.zeta.codeGenerator.model

case class AnchorWithEntities(
    anchor: Anchor,
    teamEntities: List[Entity],
    periodEntities: List[Entity],
    allEntities: List[Entity]
) {
  def name: String = anchor.name
  def team: Entity = anchor.team
  def period: Entity = anchor.period
}
