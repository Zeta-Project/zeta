const lineStyleToStrokeDasharrayMapper = {
    'dash': '10,10',
    'dot': '5,5',
    'dashdot': '10,5,5,5',
    'dashdotdot': '10,5,5,5,5,5',
};

export class StyleConfigurator {

    static configure(style) {
        const configuredStyle = Object.assign(
            StyleConfigurator.configureCommonAttributes(style),
            {
                'text': StyleConfigurator.configureTextAttributes(style)
            }
        );
        return configuredStyle;
    }

    static configureCommonAttributes(style) {
        const commonAttributes = {
            'fill': StyleConfigurator.getFill(style),
            'fill-opacity': StyleConfigurator.getFillOpacity(style),
            'stroke': StyleConfigurator.getStroke(style),
            'stroke-dasharray': StyleConfigurator.getStrokeDashArray(style),
            'stroke-opacity': StyleConfigurator.getStrokeOpacity(style),
            'stroke-width': StyleConfigurator.getStrokeWidth(style),
        };
        return StyleConfigurator.removeUndefinedValues(commonAttributes);
    }

    static configureTextAttributes(style) {
        const textAttributes = {
            'dominant-baseline': StyleConfigurator.getDominantBaseline(style),
            'fill': StyleConfigurator.getFontColor(style),
            'font-family': StyleConfigurator.getFontFamily(style),
            'font-size': StyleConfigurator.getFontSize(style),
            'font-style': StyleConfigurator.getFontStyle(style),
            'font-weight': StyleConfigurator.getFontWeight(style),
        };
        return StyleConfigurator.removeUndefinedValues(textAttributes);
    };

    // private stuff

    static getFontStyle(style) {
        return style?.font?.italic ? 'italic' : undefined;
    }

    static getFontWeight(style) {
        return style?.font?.bold ? 'bold' : 'normal';
    }

    static getFontColor(style) {
        return style?.font?.color?.hex || '#000000';
    }

    static getFontSize(style) {
        return style?.font?.size?.toString() || '20';
    }

    static getFontFamily(style) {
        return style?.font?.name || 'sans-serif';
    }

    static getDominantBaseline(style) {
        return 'text-before-edge';
    }

    static getFillOpacity(style) {
        return style?.transparency || 1.0;
    }

    static getFill(style) {
        if (style?.background?.gradient) {
            const gradient = style.background.gradient;

            const createGradientStops = () => {
                const areas = gradient.area?.map(area => ({
                    'offset': area.offset,
                    'color': area.color?.hex || '#FFFFFF'
                }));
                return areas || [];
            };

            const createGradientAttrs = () => {
                if (gradient.horizontal === undefined || gradient.horizontal) return undefined;
                return {
                    'x1': '0%',
                    'y1': '0%',
                    'x2': '0%',
                    'y2': '100%'
                };
            };

            return {
                'type': 'linearGradient',
                'stops': createGradientStops(),
                'attrs': createGradientAttrs()
            };
        }
        return style?.background?.color?.hex || '#FFFFFF';
    }

    static getStroke(style) {
        if (this.useDefaultLineAttributes(style)) {
            return '#000000';
        }
        return style?.line?.color?.hex || '#000000';
    }

    static getStrokeWidth(style) {
        if (this.useDefaultLineAttributes(style)) {
            return 1;
        }
        if (style?.line?.transparent) return undefined;
        return style?.line?.width;
    }

    static getStrokeDashArray(style) {
        if (StyleConfigurator.useDefaultLineAttributes(style)) {
            return '0';
        }
        if (!style?.line?.style) return undefined;
        return lineStyleToStrokeDasharrayMapper[style.line.style] || '0';
    }

    static getStrokeOpacity(style) {
        return style?.line?.transparent ? 0 : undefined;
    }

    static useDefaultLineAttributes(style) {
        // we have neither a color nor a transparency => use default line attributes
        return style?.line?.color?.hex === undefined && style?.line?.transparent === undefined;
    }

    static removeUndefinedValues(jsonObject) {
        return JSON.parse(JSON.stringify(jsonObject));
    }
}