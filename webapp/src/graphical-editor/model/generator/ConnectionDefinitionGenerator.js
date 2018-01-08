
class ConnectionDefinitionGenerator { 

    constructor() {
        this.placingShape = {
            'line': (shape) => this.generateLineShape(shape),
            'polyline': (shape) => this.generatePolyLineShape(shape),
            'polygon': (shape) => this.generatePolygonShape(shape),
            'rectangle': (shape, distance) => this.generateRectangleShape(shape, distance),
            'roundedRectangle': (shape, distance) => this.generateRoundedRectangleShape(shape, distance),
            'ellipse': (shape, distance) => this.generateEllipseShape(shape, distance),
            'text': (shape) => this.generateTextShape(shape)
        };

        this.generateSvgPathData = {
            'line': (shape) => this.generateLineSvgPathData(shape),
            'polyline': (shape) => this.generatePolyLineSvgPathData(shape),
            'polygon': (shape) => this.generatePolygonSvgPathData(shape),
            'rectangle': (shape, distance) => this.generateRectangleSvgPathData(shape, distance),
            'roundedRectangle': (shape, distance) => this.generateRoundedRectangleSvgPathData(shape),
            'ellipse': (shape, distance) => this.generateEllipseSvgPathData(shape),
        };
    }

    createConnectionStyle(connection) {

        const style = Object.assign(
            this.createBasicConnectionStyle(connection),
            this.handlePlacings(connection)
        )

        return style;
    }

    getStyle(styleName) {
        // Dummy -> Return a Style from StyleGenerator
        return {}
    }

    createBasicConnectionStyle(connection) {
        if ('style' in connection) {
            return this.getStyle(connection.style);
        }
        return {'.connection':{stroke: 'black'}}
    }

    createInlineStyle(connection) {
        return {};
    }

    handlePlacings(connection) {
        let placingStyle = {'.marker-target': {d: 'M 0 0'}};

        if ('placings' in connection) {
            const commonMarker = connection.placings.find((p) => p.positionOffset === 0.0 && p.shape.type !== 'text');  
            const mirroredMarker = connection.placings.find((p) => p.positionOffset === 1.0 && p.shape.type !== 'text');
            
            if (commonMarker) {
                placingStyle['.marker-source'] = this.createStyleMarkerSource(commonMarker);
            }

            if (mirroredMarker) {
                placingStyle['.marker-target'] = this.createSpecificStyleMarkerTarget(mirroredMarker);
            }            
        }
        return placingStyle;
    }

    createStyleMarkerSource(placing) {
        let shapeStyle = {};
        if ('style' in placing.shape) {
            shapeStyle = this.generateStyle(placing.shape.style);
        }
        return Object.assign(shapeStyle, this.generateMarker(placing), this.generateMarkerSourceCorrection());
    }

    createSpecificStyleMarkerTarget(placing) {
        return {};
    }

    generateMarkerSourceCorrection() {
        return {
            transform: 'scale(1,1)'
        };
    }

    generateStyleCorrections(){
        return {
            fill: 'transparent'
        };
    }

    generateMarker(placing) {
        return {
            d: this.generateSvgPathData[placing.shape.type](placing.shape)
        };
    }
    }

    generateMirroredPolyLine(shape) {
    }
    
    generateMirroredPolygon(shape) {
    }

    generateLineSvgPathData(shape) {
    }

    
    generatePolyLineSvgPathData(shape) {
    }
    
    generateRectangleSvgPathData(shape) {
    }
    
    generateRoundedRectangleSvgPathData(shape) {
    }
    
    generatePolygonSvgPathData(shape) {
    }
    generateStyle(style) {
        return {
            dummy: 'Dummy',
            text: {
                textDummy: 'Dummy'
            }
        }
    }

    createPlacingList(connection) {
        return connection.placings.map(this.createPlacing, this);
    }

    createPlacing(placing) {
        const generatedPlacing = {
            position: placing.positionOffset
        };
        return Object.assign(generatedPlacing, this.createPlacingShape(placing));
    }

    createPlacingShape(placing) {

        let placingShape = this.placingShape[placing.shape.type](placing.shape, placing.positionDistance);
        placingShape.attrs = placing.shape.type !== 'text' && 'style' in placing.shape ? Object.assign(placingShape.attrs, {style: placing.shape.style}): placingShape.attrs;
    
        return placingShape;
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
        // Braucht eine Uebergabe eines StyleGenerators
        this.connectionDefinitionGenerator = new ConnectionDefinitionGenerator();
        this.connections = connections;
    }

    getConnectionStyle(styleName) {
        const connection = this.connections.find(c => c.name === styleName);
        return connection ? this.connectionDefinitionGenerator.createConnectionStyle(connection): {};
    }

    getPlacings(styleName) {
        const connection = this.connections.find(c => c.name === styleName);
        return connection ? this.connectionDefinitionGenerator.createPlacingList(connection) : [];
    }

    getLabels(styleName) {
        const connection = this.connections.find(c => c.name === styleName);
        return connection ? this.connectionDefinitionGenerator.createLabelList(connection) : [];
    }
}