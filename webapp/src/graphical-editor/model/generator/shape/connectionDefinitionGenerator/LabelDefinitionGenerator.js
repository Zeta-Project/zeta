export default function generateLabelList(connection) {

    const labels = connection.placings.filter(placing => placing.position.offset !== 0.0 && placing.geoElement.type === 'text');
    return labels.map(generateLabel);
}

function generateLabel(placing) {
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