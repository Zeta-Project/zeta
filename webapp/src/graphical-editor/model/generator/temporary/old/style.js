//OLD
export function getStyle(stylename) {
    var style;
    switch(stylename) {

        case 'X':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                fill: '#ffffff',

                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width':1,
                'stroke-dasharray': "0"


            };
            break;

        case '(child_of -> (child_of -> X & Y) & Anonymous_Stylec7bf3fcf-d143-45df-9c90-7569922f688b)':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                fill: '#ffffff',

                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width':1,
                'stroke-dasharray': "0"


            };
            break;

        case '(child_of -> X & Y)':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                fill: '#ffffff',

                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width':1,
                'stroke-dasharray': "10,10"


            };
            break;

        case 'Y':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                fill: '#ffffff',

                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width':1,
                'stroke-dasharray': "10,10"


            };
            break;

        case 'Anonymous_Stylec7bf3fcf-d143-45df-9c90-7569922f688b':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '11',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width': 0,
                'stroke-dasharray': "0"


            };
            break;

        case 'ClassText':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '10',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                fill: '#ffffff',

                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width':1,
                'stroke-dasharray': "10,10"


            };
            break;

        case '(child_of -> XX)':
            style = {

                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },



                fill: '#ffffff',

                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width':1,
                'stroke-dasharray': "0"


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


                },



                'fill-opacity':1.0,

                stroke: '#000000',
                'stroke-width': 0,
                'stroke-dasharray': "0"


            };
            break;

        default:
            style = {};
            break;
    }
    return style;
}


export function getDiagramHighlighting(stylename) {
    var highlighting;
    switch(stylename) {

        default:
            highlighting = '';
            break;
    }
    return highlighting;
}

      