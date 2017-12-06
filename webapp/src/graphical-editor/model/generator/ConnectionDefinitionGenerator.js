

class ConnectionDefinitionGenerator {

    constructor(head) {
        this.head = ' """/*\n* This is a generated ShapeFile for JointJS\n*/\n"""'
    }

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


}