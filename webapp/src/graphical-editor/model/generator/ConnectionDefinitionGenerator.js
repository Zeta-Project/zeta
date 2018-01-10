import PlacingDefinitionGenerator from './connectionDefinitionGenerator/PlacingDefinitionGenerator'
import LabelDefinitionGenerator from './connectionDefinitionGenerator/LabelDefinitionGenerator'
import SvgDataPathGenerator from './connectionDefinitionGenerator/SvgDataPathGenerator'

class ConnectionDefinitionGenerator { 

    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;
        this.svgDataPathGenerator = new SvgDataPathGenerator();
    }

    createConnectionStyle(connection) {

        const style = Object.assign(
            this.createBasicConnectionStyle(connection),
            this.handlePlacings(connection)
        )

        return style;
    }

    getStyle(styleName) {
        return this.styleGenerator.getStyle(styleName)
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
        return Object.assign(shapeStyle, this.svgDataPathGenerator.generateMarker(placing), this.generateMarkerSourceCorrection());
    }

    createSpecificStyleMarkerTarget(placing) {
        return Object.assign(this.svgDataPathGenerator.generateMirroredMarker(placing), this.generateMarkerSourceCorrection());
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

    generateStyle(style) {
        return {
            dummy: 'Dummy',
            text: {
                textDummy: 'Dummy'
            }
        }
    }
    
    

}

export default class Generator{
    constructor(connections, styleGenerator) {
        // Braucht eine Uebergabe eines StyleGenerators
        this.connectionDefinitionGenerator = new ConnectionDefinitionGenerator(styleGenerator);
        this.connections = connections;
        this.labelDefininitonGenerator = new LabelDefinitionGenerator();
        this.placingDefinitionGenerator = new PlacingDefinitionGenerator();
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
        return connection ? this.labelDefininitonGenerator.createLabelList(connection) : [];
    }
}