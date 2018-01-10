

class LabelDefinitionGenerator {
    
    generateLabelList(connection) {
        const labels = connection.placings.filter(placing => placing.shape.type === 'label');
        return labels.map(this.generateLabel);
    }

    generateLabel(placing) {
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

export default class Generator {
    constructor() {
        this.generator = new LabelDefinitionGenerator();
    }

    createLabelList(connection) {
        return this.generator.generateLabelList(connection);
    }
}