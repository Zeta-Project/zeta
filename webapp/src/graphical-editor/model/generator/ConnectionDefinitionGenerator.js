import PlacingDefinitionGenerator from './connectionDefinitionGenerator/PlacingDefinitionGenerator'
import LabelDefinitionGenerator from './connectionDefinitionGenerator/LabelDefinitionGenerator'
import SvgDataPathGenerator from './connectionDefinitionGenerator/SvgDataPathGenerator'

class ConnectionDefinitionGenerator { 

    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;
        this.svgDataPathGenerator = new SvgDataPathGenerator();
    }

    createConnectionStyle(connection) {
        return Object.assign(
            this.createBasicConnectionStyle(connection),
            this.handlePlacings(connection)
        );
    }

    createBasicConnectionStyle(connection) {
        let basicStyle = {'.connection': {stroke: 'black'}};
        let connectionStyle = {};

        if ('style' in connection) {
            basicStyle = this.styleGenerator.getStyle(connection.style);
            connectionStyle = this.generateConnectionStyle(connection.style);
        }
        return Object.assign(basicStyle, connectionStyle);
    }

    generateConnectionStyle(style) {

        const commonAttributes = this.styleGenerator.createCommonAttributes(style);
        const fontAttributes = this.styleGenerator.createFontAttributes(style);
        return {'.connection, .marker-target, .marker-source': Object.assign(commonAttributes, fontAttributes)};
    }

    handlePlacings(connection) {
        let placingStyle = {'.marker-target': {d: 'M 0 0'}};

        if ('placings' in connection) {
            const commonMarker = connection.placings.find((p) => p.positionOffset === 0.0 && p.shape.type !== 'text');  
            const mirroredMarker = connection.placings.find((p) => p.positionOffset === 1.0 && p.shape.type !== 'text');
            
            if (commonMarker) {
                const styleMarker = this.createStyleMarkerSource(commonMarker);
                const style = this.generatePlacingStyle(commonMarker);
                placingStyle['.marker-source'] = Object.assign(styleMarker, style);
            }

            if (mirroredMarker) {
                const styleMarker = this.createSpecificStyleMarkerTarget(mirroredMarker);
                const style = this.generatePlacingStyle(mirroredMarker);
                placingStyle['.marker-target'] = Object.assign(styleMarker, style)  ;
            }            
        }
        return placingStyle;
    }

    generatePlacingStyle(placing) {
        if ('style' in placing.shape) {
            const commonAttributes = this.styleGenerator.createCommonAttributes(placing.shape.style);
            const fontAttributes = this.styleGenerator.createFontAttributes(placing.shape.style);
            return Object.assign(
                commonAttributes,
                {text: fontAttributes}
            );
        }
        return {};
    }

    createStyleMarkerSource(placing) {
        return Object.assign(this.svgDataPathGenerator.generateMarker(placing), this.generateMarkerSourceCorrection());
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

    generateInlineStyle(styleName) {
        const commonAttributes = this.styleGenerator.createCommonAttributes(styleName);
        const fontAttributes = this.styleGenerator.createFontAttributes(styleName);
        const style = {'.connection, .marker-target, .marker-source': {
            commonAttributes,
            fontAttributes
        }};

        return style;
    }
    
    

}

export default class Generator{
    //JSON extract conntact
    constructor(shape, styleGenerator) {
        this.connections = 'connections' in shape ? shape.connections : [];
        this.connectionDefinitionGenerator = new ConnectionDefinitionGenerator(styleGenerator);
        this.labelDefininitonGenerator = new LabelDefinitionGenerator();
        this.placingDefinitionGenerator = new PlacingDefinitionGenerator(styleGenerator);
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