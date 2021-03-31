package de.htwg.zeta.common.models.project.gdsl.shape.geomodel

import de.htwg.zeta.common.models.project.gdsl.style.Style

case class Hexagon(
                    size: Size,
                    position: Position,
                    childGeoModels: List[GeoModel],
                    style: Style
                  ) extends GeoModel
