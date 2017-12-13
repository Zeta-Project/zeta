

class ConnectionDefinitionGenerator { 

    generate(connections) {
        let result = {}
        connections.map(function(connection) {
            testFunction(connection)
            result.push(createConnection(connection))
        })
        return result;
    }

    createConnection(connections) {
        let styleConnection = {}
        connections.map(function(connection) {
            if (connection.style.isDefined) {
                styleConnection[connection] = connection.style.name
            } else {

            }
            styleConnection[connection] = connection.style
        })
        return styleConnection
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

    }

    getLabels(styleName) {
        const connection = this.connections.filter(c => c.name === styleName);
        return connection.length === 1 ? this.connectionDefinitionGenerator.createLabelList(connection.pop()) : []
    }
}
