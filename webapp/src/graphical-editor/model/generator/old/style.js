function
getStyle(stylename) {
    var style;
    switch (stylename) {
        case
        '(child_of -> DefaultDefault)'
        :
            style = {
                text: {
                    'dominant-baseline': "text-before-edge", 'font-family': 'sans-serif', 'font-size': '20', 'fill': '#000000', 'font-weight': ' normal'
                }, fill: '#ffffff', 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 1, 'stroke-dasharray': "0"
            };
            break;
        case 'Default':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'
                }, fill: '#ffffff', 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 1, 'stroke-dasharray': "0"
            };
            break;
        case 'Dashed':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '11',
                    'fill': '#000000',
                    'font-weight': ' normal'
                }, 'fill-opacity': 1.0, stroke: '#808080', 'stroke-width': 1, 'stroke-dasharray': "10,10"
            };
            break;
        case 'Dotted':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '11',
                    'fill': '#000000',
                    'font-weight': ' normal'
                }, 'fill-opacity': 1.0, stroke: '#808080', 'stroke-width': 1, 'stroke-dasharray': "5,5"
            };
            break;
        case 'Red':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '14',
                    'fill': '#ffffff',
                    'font-weight': ' normal'
                }, fill: '#ff0000', 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 1, 'stroke-dasharray': "0"
            };
            break;
        case 'Blue':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '14',
                    'fill': '#ffffff',
                    'font-weight': ' normal'
                }, fill: '#0000ff', 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 1, 'stroke-dasharray': "0"
            };
            break;
        case '(child_of -> Default & Blue)':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '14',
                    'fill': '#ffffff',
                    'font-weight': ' normal'
                }, fill: '#0000ff', 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 1, 'stroke-dasharray': "0"
            };
            break;
        case '(child_of -> Default & Dotted)':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'
                }, fill: '#ffffff', 'fill-opacity': 1.0, stroke: '#808080', 'stroke-width': 1, 'stroke-dasharray': "5,5"
            };
            break;
        case '(child_of -> Default & Red)':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '14',
                    'fill': '#ffffff',
                    'font-weight': ' normal'
                }, fill: '#ff0000', 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 1, 'stroke-dasharray': "0"
            };
            break;
        case 'rootStyle':
            style = {
                text: {
                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '11',
                    'fill': '#000000',
                    'font-weight': ' normal'
                }, 'fill-opacity': 1.0, stroke: '#000000', 'stroke-width': 0, 'stroke-dasharray': "0"
            };
            break;
        default:
            style = {};
            break;
    }
    return style;
}

function getDiagramHighlighting(stylename) {
    var highlighting;
    switch (stylename) {
        default:
            highlighting = '';
            break;
    }
    return highlighting;
}