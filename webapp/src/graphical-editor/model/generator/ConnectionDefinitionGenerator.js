const newLocal = 'style';


class ConnectionDefinitionGenerator { 

    generate(connections) {
        let result = {}
        connections.map(function(connection) {
            result.push(createConnection(connection))
        })
        return result;
    }

    createPlacingList(connection) {
        const result = connection.placings.map(this.createPlacing, this);
        return result;
    }

    createPlacing(placing) {
        const generatedPlacing = {
            position: placing.positionOffset
        }
        return Object.assign(generatedPlacing, this.createPlacingShape(placing));
    }

    createPlacingShape(placing) {
        const shape = placing.shape;

        switch(shape.type) {
            case 'Line': return this.generateLineShape(shape);
            case 'PolyLine': return this.generatePolyLineShape(shape);
            case 'Rectangle': return this.generateRectangleShape(shape, placing.positionDistance);
            case 'RoundedRectangle': return this.generateRoundedRectangleShape(shape, placing.positionDistance);
            case 'Ellipse': return this.generateEllipseShape(shape, placing.positionDistance);    
            case 'Text': return this.generateTextShape(shape);        
            
        }
    }

    styleGetCommonAttributes(style) {
        // TODO 
        return {"style": "dummy"}
    }

    generatePlacingShapeStyle(generatedShape, shape) {
        generatedShape.attrs = 'style' in shape ? Object.assign(generatedShape.attrs, this.styleGetCommonAttributes(shape.style)): generatedShape.attrs;
        return generatedShape;
    }

    generateLineShape(line) {
        const shape = {
            markup: '<line />',
            attrs: {
                x1: line.startPoint.x,
                y1: line.startPoint.y,
                x2: line.endPoint.x,
                y2: line.endPoint.y
            }
        };
        return this.generatePlacingShapeStyle(shape, line)
    }

    generatePolyLineShape(shape) {
        const polyLineShape = {
            markup: '<polyline />',
            attrs: {
                points: [
                    shape.points.map(function(point) {
                        
                    })
                       //+ shape.points.map(point => point.x + ", " + point.y + { if (point != shape.points.last) " " else "\"" }).mkString("") + raw
                ],
                fill: 'transparent'
            }
        };  

        if ('style' in polyLineShape) {
            return Object.assign(polyLineShape.attrs, this.stylegetCommonAttributes(shape.style));
        };
        return polyLineShape;
    }

    generatePoints(points) {
        let pointString = "";

        points.map(function(point) {
            pointString += (`${point.x}, ${point.y}, `)
        })
        pointString.pop().pop();
        pointString += "/";
    }
    
    generateRectangleShape(rectangle, distance) {
        const shape = {
            markup: '<rect />',
            attrs:{
                height: rectangle.sizeHeight,
                width: rectangle.sizeWidth,
                y: distance - rectangle.sizeHeight / 2
            }
        };
        return this.generatePlacingShapeStyle(shape, rectangle)
        
    }
    
    generateRoundedRectangleShape(roundedRectangle, distance) {
        const shape = {
            markup: '<rect />',
            attrs:{
                height: roundedRectangle.sizeHeight,
                width: roundedRectangle.sizeWidth,
                rx: roundedRectangle.curveWidth,
                ry: roundedRectangle.curveHeight,
                y: distance - roundedRectangle.sizeHeight / 2,
            }
        }
        return this.generatePlacingShapeStyle(shape, roundedRectangle)        
      }
    
      /*
      private def generatePlacingShape(shape: Polygon, distance: Int) = {
        """
        markup: '<polygon />',
        attrs:{
          points: """" + shape.points.map(point => point.x + "," + point.y + { if (point != shape.points.last) " " else "\"" }).mkString + raw""",
          ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""}
        }
        """
      }*/
    
    generateEllipseShape(ellipse, distance) {

    }
    
    generateTextShape(text) {

    }
    
    createLabelList(connection) {
        const labels = connection.placings.filter(placing => placing.shape.type === 'Label');
        return labels.map(this.createLabel);
    }

    createLabel(placing) {
        return {
            position: placing.positionOffset,
            attrs: {
              rect: {fill: 'transparent'},
              text: {
                y: 'positionDistance' in placing ? placing.positionDistance : 0,
                text: placing.shape.textBody
              }
            },
            id: placing.shape.id
        };
    }

}

export default class Generator{
    constructor(connections) {
        this.connectionDefinitionGenerator = new ConnectionDefinitionGenerator()
        this.connections = connections
    }

    getConnectionStyle(styleName) {
        
    }

    getPlacings(styleName) {
        const connection = this.connections.filter(c => c.name === styleName);
        return connection.length === 1 ? this.connectionDefinitionGenerator.createPlacingList(connection.pop()) : []
    }

    getLabels(styleName) {
        const connection = this.connections.filter(c => c.name === styleName);
        return connection.length === 1 ? this.connectionDefinitionGenerator.createLabelList(connection.pop()) : []
    }
}
