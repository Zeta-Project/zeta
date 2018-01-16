
class PlacingDefinitionGenerator {
    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;

        //TODO TypenMapping prüfen ob type existiert -> Fehlermeldung Exception

        this.placingShape = {
            'line': (shape) => this.generateLineShape(shape),
            'polyline': (shape) => this.generatePolyLineShape(shape),
            'polygon': (shape) => this.generatePolygonShape(shape),
            'rectangle': (shape, distance) => this.generateRectangleShape(shape, distance),
            'roundedRectangle': (shape, distance) => this.generateRoundedRectangleShape(shape, distance),
            'ellipse': (shape, distance) => this.generateEllipseShape(shape, distance),
            'text': (shape) => this.generateTextShape(shape)
        };
    }
    
    generatePlacingList(connection) {
        //TODO Filter if Offste != 0.0 und kein Text

        const placings = connection.placings.filter((p) => p.)
        console.log(connection);
        return connection.placings.map(this.generatePlacing, this);
    }

    generatePlacing(placing) {
        const generatedPlacing = {
            position: placing.positionOffset
        };
        return Object.assign(generatedPlacing, this.createPlacingShape(placing));
    }

    createPlacingShape(placing) {

        let placingShape = this.placingShape[placing.shape.type](placing.shape, placing.positionDistance);
        placingShape.attrs = placing.shape.type !== 'text' && 'style' in placing.shape ? Object.assign(placingShape.attrs, this.getCommonAttributesStyle(placing)): placingShape.attrs;
    
        return placingShape;
    }

    getCommonAttributesStyle(placing) {
        return this.styleGenerator.createCommonAttributes(placing.shape.style);
    }

    generateLineShape(line) {
        return {
            markup: '<line />',
            attrs: {
                x1: line.startPoint.x,
                y1: line.startPoint.y,
                x2: line.endPoint.x,
                y2: line.endPoint.y
            }
        };
    }

    generatePolyLineShape(shape) {
        return {
            markup: '<polyline />',
            attrs: {
                points: this.generatePoints(shape.points),
                fill: 'transparent'
            }
        };          
    }

    generatePoints(points) {
        let pointString = "";
        points.map(function(point) {
            pointString += (`${point.x},${point.y} `)
        })
        return pointString.trim();
    }
    
    generateRectangleShape(rectangle, distance) {
        return {
            markup: '<rect />',
            attrs:{
                height: rectangle.sizeHeight,
                width: rectangle.sizeWidth,
                y: distance - rectangle.sizeHeight / 2
            }
        };        
    }
    
    generateRoundedRectangleShape(roundedRectangle, distance) {
        return {
            markup: '<rect />',
            attrs:{
                height: roundedRectangle.sizeHeight,
                width: roundedRectangle.sizeWidth,
                rx: roundedRectangle.curveWidth,
                ry: roundedRectangle.curveHeight,
                y: distance - roundedRectangle.sizeHeight / 2,
            }
        }
    }
      
    generatePolygonShape(polygon) {
        return {
            markup: '<polygon />',
            attrs:{
              points: this.generatePoints(polygon.points)          
            }
        }
    }
    
    generateEllipseShape(ellipse, distance) {
        return {
            markup: '<ellipse />',
            attrs:{
                rx: ellipse.sizeWidth / 2,
                ry: ellipse.sizeHeight / 2,
                cy: distance,
            }
        };
    }
    
    generateTextShape(text) {
        return {
            markup: `<text>${text.textBody}</text>`,
            attrs:{
                y: text.sizeHeight / 2
            }
        };
    }
}

export default class Generator {
    constructor(styleGenerator) {
        this.generator = new PlacingDefinitionGenerator(styleGenerator);
    }

    createPlacingList(connection) {
        return this.generator.generatePlacingList(connection);
    }
}