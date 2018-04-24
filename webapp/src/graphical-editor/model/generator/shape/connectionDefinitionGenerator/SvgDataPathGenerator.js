
function replaceAll(str, find, replace) {
    return str.replace(new RegExp(find, 'g'), replace);
}

class SvgDataPathGenerator {
    
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

    generateMirroredPolyLine(shape) {
        const mirroredPoints = shape.points.map(function(p) {return {x: p.x * -1, y: p.y}});
        const head = mirroredPoints[0];
        const tail = mirroredPoints.slice(1);

        return (`M ${head.x} ${head.y} ` + tail.map(point => `L ${point.x} ${point.y}`)).replace(/,/g, "");
    }
    
    generateMirroredPolygon(shape) {
        const mirroredPoints = shape.points.map(function(p) {return {x: p.x * -1, y: (p.y * -1)}});
        const head = mirroredPoints[0];
        const tail = mirroredPoints.slice(1);

        return (`M ${head.x} ${head.y} `+ tail.map(p => `L ${p.x} ${p.y}`) + 'z').replace(/,/g, "");
    }

    generateLineSvgPathData(shape) {
        return `M ${shape.startPoint.x} ${shape.startPoint.y} L ${shape.endPoint.x} ${shape.endPoint.y}`
    }

    
    generatePolyLineSvgPathData(shape) {
        const head = shape.points[0];
        const tail = shape.points.slice(1);
        
        return (`M ${head.x} ${head.y} ` + tail.map(point => `L ${point.x} ${point.y}`)).replace(/,/g, "");
    }
    
    generateRectangleSvgPathData(shape) {
        return `M ${shape.position.x} ${shape.position.y}l ${shape.sizeWidth} 0 l 0 ${shape.sizeHeight} l -${shape.sizeWidth} 0 z`
    }
    
    generateRoundedRectangleSvgPathData(shape) {
        return `M ${shape.position.x} ${shape.curveWidth} ${shape.position.y} ${shape.curveHeight} l ${(shape.sizeWidth - 2 * shape.curveWidth)}` +
        `l 0 a ${shape.curveWidth} ${shape.curveHeight} 0 0 1 ${shape.curveWidth} ${shape.curveHeight}l 0 ${(shape.sizeHeight - 2 * shape.curveHeight)}` +
        ` a ${shape.curveWidth} ${shape.curveHeight} 0 0 1 -${shape.curveWidth} ${shape.curveHeight} l -${(shape.sizeWidth - 2 * shape.curveWidth)} 0 a ` +
        `${shape.curveWidth} ${shape.curveHeight} 0 0 1 -${shape.curveWidth} -${shape.curveHeight} l 0 -${(shape.sizeHeight - 2 * shape.curveHeight)} a ` +
        `${shape.curveWidth} ${shape.curveHeight} 0 0 1 ${shape.curveWidth} -${shape.curveHeight}`
    }
    
    generatePolygonSvgPathData(shape) {
        const head = shape.points[0];
        const tail = shape.points.slice(1);

        return (`M ${head.x} ${head.y} ` + tail.map(p => `L ${p.x} ${p.y}`)).replace(/,/g, "") + 'z'
    }
    
    generateEllipseSvgPathData(shape) {
        const rx = shape.sizeWidth / 2;
        const ry = shape.sizeHeight / 2;
        return `M ${shape.position.x} ${shape.position.y} a  ${rx} ${ry} 0 0 1 ${rx} -${ry} a  ${rx} ${ry} 0 0 1 ${rx} ${ry} a  ${rx} ${ry} 0 0 1 -${rx} ${ry} a  ${rx} ${ry} 0 0 1 -${rx} -${ry}`
    }
}

export default class Generator {

    constructor() {
        this.svgDataPathGenerator = new SvgDataPathGenerator();
    }

    generateMarker(placing) {
        const placingType = placing.geoElement.type;
        if (placingType in this.svgDataPathGenerator.generateSvgPathData) {
            return {d: this.svgDataPathGenerator.generateSvgPathData[placing.geoElement.type](placing.geoElement)};
        }
        throw new Error(`Unknown placing: ${placingType}`);        
    }

    generateMirroredMarker(placing) {
        const type = placing.geoElement.type;

        let marker;
        if (type === 'polygon') {
            marker = {d: this.svgDataPathGenerator.generateMirroredPolygon(placing.geoElement)};
        } else if (type === 'polyline') {
            marker = {d: this.svgDataPathGenerator.generateMirroredPolyLine(placing.geoElement)};
        } else {
            marker = this.generateMarker(placing);
        }
        return marker;
    } 
}