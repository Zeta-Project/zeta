import ConnectionDefinitionGenerator from './ConnectionDefinitionGenerator';

describe('getStyle', () => {
    function create(styles) {
        const generator = new StyleGenerator(styles)
        return (styleName) => generator.getStyle(styleName);
    }

    test('with style not found', () => {
        const getStyle = create([]);
        expect(getStyle('DefaultStyle')).toEqual({});
    });
});