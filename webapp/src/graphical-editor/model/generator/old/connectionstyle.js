//OLD
function getConnectionStyle(stylename) {
    var style;
    switch (stylename) {

        case 'inheritance':
            style = getStyle('(child_of -> XX)');


            //Get inline style
            var inline = {
                '.connection, .marker-target, .marker-source': {


                    fill: '#ffffff',

                    'fill-opacity': 1.0,

                    stroke: '#000000',
                    'stroke-width': 1,
                    'stroke-dasharray': "0"

                    ,

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                }
            };

            //Merge with default style
            jQuery.extend(style, inline);

            style['.marker-target'] = {
                d: 'M 10 -10 L 0 0L 10 10z'
                ,


                fill: '#ffffff',

                'fill-opacity': 1.0,

                stroke: '#000000',
                'stroke-width': 1,
                'stroke-dasharray': "0"

                ,
                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },
            };
            break;

        case 'realization':
            style = getStyle('(child_of -> X & Y)');


            //Get inline style
            var inline = {
                '.connection, .marker-target, .marker-source': {


                    fill: '#ffffff',

                    'fill-opacity': 1.0,

                    stroke: '#000000',
                    'stroke-width': 1,
                    'stroke-dasharray': "10,10"

                    ,

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                }
            };

            //Merge with default style
            jQuery.extend(style, inline);

            style['.marker-target'] = {
                d: 'M 10 -10 L 0 0L 10 10z'
                ,


                fill: '#ffffff',

                'fill-opacity': 1.0,

                stroke: '#000000',
                'stroke-width': 1,
                'stroke-dasharray': "0"

                ,
                text: {

                    'dominant-baseline': "text-before-edge",
                    'font-family': 'sans-serif',
                    'font-size': '20',
                    'fill': '#000000',
                    'font-weight': ' normal'


                },
            };
            break;

        default:
            style = {};
            break;
    }

    return style;
}

function getPlacings(stylename) {
    var placings;
    switch (stylename) {

        default:
            placings = [];
            break;
    }

    return placings;
}

function getLabels(stylename) {
    var labels;
    switch (stylename) {

        default:
            labels = [];
            break;
    }

    return labels;
}
    