const lineStyleToStrokeDasharrayMapper = {
    'dash': '10,10',
    'dot': '5,5',
    'dashdot': '10,5,5,5',
    'dashdotdot': '10,5,5,5,5,5',
};

class StyleGenerator {

    generate(style) {
        return Object.assign(
            this.createMandatoryAttributes(style),
            this.createBackgroundAttribute(style),
            this.createLineAttributes(style)
        );
    }

    createCommonAttributes(style) {
        return Object.assign(this.createBackgroundAttribute(style), this.createLineAttributes(style));
    }

    createMandatoryAttributes(style) {
        return {
            'text': Object.assign(this.createTextAttributes(style.font), this.createOptionalFontStyle(style.font))
        };
    }

    createTextAttributes(font) {
        return {
            'dominant-baseline': "text-before-edge",
            'font-family': font === undefined || font.name === undefined ? 'sans-serif' : font.name,
            'font-size': font === undefined || font.size === undefined ? '11' : font.size.toString(),
            'fill': font === undefined || font.color === undefined ? 'rgba(0,0,0,1.0)' : font.color,
            'font-weight': font === undefined || font.bold === undefined || !font.bold ? 'normal' : 'bold',
        }
    }

    createOptionalFontStyle(font) {
        return font === undefined || font.italic === undefined || !font.italic ? {} : {'font-style': 'italic'};
    }

    createBackgroundAttribute(style) {
        return style.background === undefined ? {} : {'fill': this.createBackground(style.background)};
    }

    createBackground(background) {
        return background.gradient === undefined ? background.color : this.createBackgroundGradient(background.gradient);
    }

    createBackgroundGradient(gradient) {
        return Object.assign(
            {
                'type': 'linearGradient',
                'stops': gradient.area === undefined ? [] : gradient.area.map((e) => this.createGradientStop(e)),
            },
            this.createVerticalGradient(gradient)
        );
    }

    createGradientStop(stop) {
        return {
            'offset': stop.offset,
            'color': stop.color,
        }
    }

    createVerticalGradient(gradient) {
        return gradient.horizontal === undefined || gradient.horizontal ? {} : {
            'attrs': {
                'x1': '0%',
                'y1': '0%',
                'x2': '0%',
                'y2': '100%'
            }
        };
    }

    createLineAttributes(style) {
        return style.line === undefined || style.line.color === undefined ? this.createDefaultLineAttributes() : this.createColorLineAttributes(style);
    }

    createDefaultLineAttributes() {
        return {
            'stroke': '#000000',
            'stroke-width': 0,
            'stroke-dasharray': "0",
        };
    }

    createColorLineAttributes(style) {
        return Object.assign(
            {
                'stroke': style.line.color,
            },
            this.createLineWidth(style.line),
            this.createLineStyle(style.line),
        );
    }

    createLineWidth(line) {
        return line.width === undefined ? {} : {'stroke-width': line.width};
    }

    createLineStyle(line) {
        return line.style === undefined ? {} : {'stroke-dasharray': this.mapLineStyleToStrokeDasharray(line.style)}
    }

    mapLineStyleToStrokeDasharray(lineStyle) {
        return lineStyleToStrokeDasharrayMapper[lineStyle] ? lineStyleToStrokeDasharrayMapper[lineStyle] : '0';
    }
}

/**
 * Generator of styles information for JointJS
 */
export default class Generator {
    constructor(styles) {
        this.styles = styles;
        this.styleGenerator = new StyleGenerator();
    }

    getStyle(styleName) {
        const style = this.styles.find((s) => s.name === styleName);
        return style ? this.styleGenerator.generate(style) : {};
    }

    createCommonAttributes(styleName) {
        const style = this.styles.find((s) => s.name === styleName);
        return style ? this.styleGenerator.createCommonAttributes(style) : {};
    }

    createFontAttributes(styleName) {
        const style = this.styles.find((s) => s.name === styleName);
        return style ? this.styleGenerator.createTextAttributes(style) : {};
    }
}