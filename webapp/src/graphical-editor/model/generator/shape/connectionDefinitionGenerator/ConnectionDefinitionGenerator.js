import PlacingDefinitionGenerator from './PlacingDefinitionGenerator'
import createLabelList from './LabelDefinitionGenerator'
import SvgDataPathGenerator from './SvgDataPathGenerator'

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
            const commonMarker = connection.placings.find((p) => p.position.offset === 0.0 && p.geoElement.type !== 'text');
            const mirroredMarker = connection.placings.find((p) => p.position.offset === 1.0 && p.geoElement.type !== 'text');
            
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
        if ('style' in placing.geoElement) {
            const commonAttributes = this.styleGenerator.createCommonAttributes(placing.geoElement.style);
            const fontAttributes = this.styleGenerator.createFontAttributes(placing.geoElement.style);
            return Object.assign(
                commonAttributes,
                    {text: fontAttributes}
            );
        }
        return {};
    }

    createStyleMarkerSource(placing) {
        return Object.assign(this.svgDataPathGenerator.generateMarker(placing), ConnectionDefinitionGenerator.generateMarkerSourceCorrection());
    }

    createSpecificStyleMarkerTarget(placing) {
        return Object.assign(
            this.svgDataPathGenerator.generateMirroredMarker(placing),
            this.generatePlacingStyle(placing),
            ConnectionDefinitionGenerator.generateMarkerSourceCorrection()
        );
    }

    static generateMarkerSourceCorrection() {
        return {
            transform: 'scale(1,1)'
        };
    }

}

export default class Generator{

    constructor(shape, styleGenerator) {
        this.connections = shape.edges ? shape.edges : [];
        this.connectionDefinitionGenerator = new ConnectionDefinitionGenerator(styleGenerator);
        this.placingDefinitionGenerator = new PlacingDefinitionGenerator(styleGenerator);
    }

    getConnectionStyle(connectionName) {
        const connection = this.connections.find(c => c.name === connectionName);
        return connection ? this.connectionDefinitionGenerator.createConnectionStyle(connection): {};
    }

    getPlacings(connectionName) {
        const connection = this.connections.find(c => c.name === connectionName);
        return connection ? this.placingDefinitionGenerator.createPlacingList(connection) : [];
    }

    getLabels(connectionName) {
        const connection = this.connections.find(c => c.name === connectionName);
        return connection ? createLabelList(connection) : [];
    }
}