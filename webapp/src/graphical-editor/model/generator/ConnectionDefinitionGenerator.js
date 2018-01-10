
import PlacingDefinitionGenerator from './connectionDefinitionGenerator/PlacingDefinitionGenerator'

class ConnectionDefinitionGenerator { 

    constructor() {

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
        return Object.assign(this.generateMirroredMarker(placing), this.generateMarkerSourceCorrection());
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

    generateMirroredMarker(placing) {
        const type = placing.shape.type;

        let marker;
        if (type === 'polygon') {
            marker = this.generateMirroredPolygon(placing.shape);
        } else if (type === 'polyline') {
            marker = this.generateMirroredPolyLine(placing.shape);
        } else {
            marker = this.generateMarker(placing);
        }
        return {d: marker};
    }

    generateMirroredPolyLine(shape) {
        const mirroredPoints = shape.points.map(function(p) {return {x: p.x * -1, y: p.y}});
        const head = mirroredPoints[0];
        const tail = mirroredPoints.slice(1);

        const res = ("M " + head.x + " " + head.y + " " + tail.map(point => "L " + point.x + " " + point.y)).replace(",", "");
        return res;
    }
    
    generateMirroredPolygon(shape) {
        const mirroredPoints = shape.points.map(function(p) {return {x: p.x * -1, y: (p.y * -1)}})
        const head = mirroredPoints[0];
        const tail = mirroredPoints.slice(1);

        return ("M " + head.x + " " + head.y + " " + tail.map(p => "L " + p.x + " " + p.y) + "z").replace(",", "");
    }

    generateLineSvgPathData(shape) {
        return "M " + shape.startPoint.x + " " + shape.startPoint.y + " L " + shape.endPoint.x + " " + shape.endPoint.y
    }

    
    generatePolyLineSvgPathData(shape) {
        const head = shape.points[0];
        const tail = shape.points.slice(1);
        
        return ("M " + head.x + " " + head.y + " " + tail.map(point => "L " + point.x + " " + point.y)).replace(",", "");
    }
    
    generateRectangleSvgPathData(shape) {
        return "M " + shape.position.x + " " + shape.position.y + "l " + shape.sizeWidth + " 0 l 0 " + shape.sizeHeight + " l -" + shape.sizeWidth + " 0 z"
    }
    
    generateRoundedRectangleSvgPathData(shape) {
        return "M " + shape.position.x + " " + shape.curveWidth + " " + shape.position.y + " " + shape.curveHeight + " l " + (shape.sizeWidth - 2 * shape.curveWidth) +
          "l 0 a " + shape.curveWidth + " " + shape.curveHeight + " 0 0 1 " + shape.curveWidth + " " + shape.curveHeight + "l 0 " +
          (shape.sizeHeight - 2 * shape.curveHeight) + " a " + shape.curveWidth + " " + shape.curveHeight + " 0 0 1 -" + shape.curveWidth +
          " " + shape.curveHeight + " l -" + (shape.sizeWidth - 2 * shape.curveWidth) + " 0 a " + shape.curveWidth + " " + shape.curveHeight +
          " 0 0 1 -" + shape.curveWidth + " -" + shape.curveHeight + " l 0 -" + (shape.sizeHeight - 2 * shape.curveHeight) +
          " a " + shape.curveWidth + " " + shape.curveHeight + " 0 0 1 " + shape.curveWidth + " -" + shape.curveHeight
    }
    
    generatePolygonSvgPathData(shape) {
        const head = shape.points[0];
        const tail = shape.points.slice(1);

        return ("M " + head.x + " " + head.y + " " + tail.map(p => "L " + p.x + " " + p.y)).replace(",", "") + "z"
    }
    
    generateEllipseSvgPathData(shape) {
        const rx = shape.sizeWidth / 2
        const ry = shape.sizeHeight / 2
        return "M " + shape.position.x + " " + shape.position.y + " a  " + rx + " " + ry + " 0 0 1 " + rx + " -" + ry + " a  " + rx + " " + ry + " 0 0 1 " + rx + " " +
          ry + " a  " + rx + " " + ry + " 0 0 1 -" + rx + " " + ry + " a  " + rx + " " + ry + " 0 0 1 -" + rx + " -" + ry
    }

    generateStyle(style) {
        return {
            dummy: 'Dummy',
            text: {
                textDummy: 'Dummy'
            }
        }
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
        this.placingDefinitionGenerator = new PlacingDefinitionGenerator(connections);
        this.connections = connections;
    }

    getConnectionStyle(styleName) {
        const connection = this.connections.find(c => c.name === styleName);
        return connection ? this.connectionDefinitionGenerator.createConnectionStyle(connection): {};
    }

    getPlacings(styleName) {
        const connection = this.connections.find(c => c.name === styleName);
        return connection ? this.placingDefinitionGenerator.createPlacingList(connection) : [];
    }

    getLabels(styleName) {
        const connection = this.connections.find(c => c.name === styleName);
        return connection ? this.connectionDefinitionGenerator.createLabelList(connection) : [];
    }
}