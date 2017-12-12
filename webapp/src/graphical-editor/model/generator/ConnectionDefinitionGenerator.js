

class ConnectionDefinitionGenerator { 

    generate(connections) {
        let result = {}
        connections.foreach(function(connection) {
            testFunction(connection)
            result.push(createConnection(connection))
        })
        return result;
    }

    getConnectionStyle(styleName) {
        
    }

    getPlacings(styleName) {

    }

    getLabels(styleName) {

    }

    testFunction(connection) {
        console.log(connection)
    }

    createConnection(connection) {
        return connection;
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

    createLabel(placing) {
        return {
            position: placing.positionOffset,
            attrs: {
              rect: {fill: 'transparent'},
              text: {
                y: placing.positionDistance.getOrElse(0),
                text: placing.shape.textBody
              }
            },
            id: placing.shape.id
          }
    }


}

export default class Generator{
    constructor(connections) {
        this.connections = connections
        this.connectionDefinitionGenerator = new C
    }

    getConnectionStyle(styleName) {
        
    }

    getPlacings(styleName) {

    }

    getLabels(styleName) {

    }
}
