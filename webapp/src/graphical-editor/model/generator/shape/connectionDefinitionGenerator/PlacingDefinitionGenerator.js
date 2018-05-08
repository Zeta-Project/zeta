
class PlacingDefinitionGenerator {
    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;
        
        this.placingShape = {
            'line': (shape) => this.generateLineShape(shape),
            'polyline': (shape) => this.generatePolyLineShape(shape),
            'polygon': (shape) => this.generatePolygonShape(shape),
            'rectangle': (shape, distance) => this.generateRectangleShape(shape, distance),
            'roundedRectangle': (shape, distance) => this.generateRoundedRectangleShape(shape, distance),
            'ellipse': (shape, distance) => this.generateEllipseShape(shape, distance),
            'textfield': (shape) => this.generateTextShape(shape),
            'statictext': (shape) => this.generateStaticTextShape(shape)
        };
    }
    
    generatePlacingList(connection) {
        const placings = connection.placings.filter((p) => p.position.offset !== 0.0 && p.position.offset !== 1.0);
        return placings.map(this.generatePlacing, this);
    }

    generatePlacing(placing) {
        const generatedPlacing = {
            position: placing.position.offset
        };
        return Object.assign(generatedPlacing, this.createPlacingShape(placing));
    }

    createPlacingShape(placing) {

        const placingType = placing.geoElement.type;
        if (placingType in this.placingShape) {
            let placingShape = this.placingShape[placingType](placing.geoElement, placing.position.distance);
            placingShape.attrs = placingType !== 'text' && 'style' in placing.geoElement ? Object.assign(placingShape.attrs, this.getCommonAttributesStyle(placing)): placingShape.attrs;
    
            return placingShape;
        }
        throw new Error(`Unknown placing: ${placingType}`);
    }

    getCommonAttributesStyle(placing) {
        return this.styleGenerator.createCommonAttributes(placing.geoElement.style);
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
                height: rectangle.size.height,
                width: rectangle.size.width,
                y: distance - rectangle.size.height / 2
            }
        };        
    }
    
    generateRoundedRectangleShape(roundedRectangle, distance) {
        return {
            markup: '<rect />',
            attrs:{
                height: roundedRectangle.size.height,
                width: roundedRectangle.size.width,
                rx: roundedRectangle.curve.width,
                ry: roundedRectangle.curve.height,
                y: distance - roundedRectangle.size.height / 2,
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
                rx: ellipse.size.width / 2,
                ry: ellipse.size.height / 2,
                cy: distance,
            }
        };
    }
    
    generateTextShape(text) {
        return {
            markup: `<text>${text.textBody}</text>`,
            attrs:{
                x: text.position.x,
                y: text.position.y
            }
        };
    }

    generateStaticTextShape(text) {
        return {
            markup: `<text>${text.text}</text>`,
            attrs:{
                x: text.position.x,
                y: text.position.y
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