const newLocal = 'style';


class ConnectionDefinitionGenerator { 

    generate(connections) {
        let result = {}
        connections.map(function(connection) {
            result.push(createConnection(connection))
        })
        return result;
    }

    createPlacingList(connection) {
        const result = connection.placings.map(this.createPlacing, this);
        return result;
    }

    createPlacing(placing) {
        const generatedPlacing = {
            position: placing.positionOffset
        }
        return Object.assign(generatedPlacing, this.createPlacingShape(placing));
    }

    createPlacingShape(placing) {
        const shape = placing.shape;

        switch(shape.type) {
            case 'Line': return this.generateLineShape(shape);
            case 'PolyLine': return this.generatePolyLineShape(shape);
            case 'Rectangle': return this.generateRectangleShape(shape, placing.positionDistance);
            case 'RoundedRectangle': return this.generateRoundedRectangleShape(shape, placing.positionDistance);
            case 'Ellipse': return this.generateEllipseShape(shape, placing.positionDistance);    
            case 'Text': return this.generateTextShape(shape);        
            
        }
    }

    generateLineShape(line) {
        
    }

    generatePolyLineShape(shape) {
        
    }

    
    generateRectangleShape(rectangle, distance) {
        
    }
    
    generateRoundedRectangleShape(roundedRectangle, distance) {
     
    }
    
      /*
      private def generatePlacingShape(shape: Polygon, distance: Int) = {
        """
        markup: '<polygon />',
        attrs:{
          points: """" + shape.points.map(point => point.x + "," + point.y + { if (point != shape.points.last) " " else "\"" }).mkString + raw""",
          ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""}
        }
        """
      }*/
    
    generateEllipseShape(ellipse, distance) {

    }
    
    generateTextShape(text) {

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
        const connection = this.connections.filter(c => c.name === styleName);
        return connection.length === 1 ? this.connectionDefinitionGenerator.createPlacingList(connection.pop()) : []
    }

    getLabels(styleName) {
        const connection = this.connections.filter(c => c.name === styleName);
        return connection.length === 1 ? this.connectionDefinitionGenerator.createLabelList(connection.pop()) : []
    }
}
