import StencilGenerator from './StencilGenerator';

describe('Stencil.groups', () => {
    test('without palettes', () => {
        const Stencil = new StencilGenerator({});
        expect(Stencil.groups).toEqual({});
    });

    test('with single palette', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'GENERAL',
                    }
                ]
            }
        });
        expect(Stencil.groups).toEqual({
            'gENERAL': {
                index: 1,
                label: 'GENERAL',
            },
        });
    });

    test('with multiple palettes', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'GENERAL',
                    },
                    {
                        palette: 'CUSTOM',
                    }
                ]
            }
        });
        expect(Stencil.groups).toEqual({
            'gENERAL': {
                index: 1,
                label: 'GENERAL',
            },
            'cUSTOM': {
                index: 2,
                label: 'CUSTOM',
            },
        });
    });

    test('with duplicate palettes', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'GENERAL',
                    },
                    {
                        palette: 'GENERAL',
                    },
                    {
                        palette: 'CUSTOM',
                    }
                ]
            }
        });
        expect(Stencil.groups).toEqual({
            'gENERAL': {
                index: 1,
                label: 'GENERAL',
            },
            'cUSTOM': {
                index: 2,
                label: 'CUSTOM',
            },
        });
    });
});