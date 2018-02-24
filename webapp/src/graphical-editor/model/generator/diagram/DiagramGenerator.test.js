import DiagramGenerator from './DiagramGenerator';

describe('getDiagram', () => {

    function create(diagrams) {
        const generator = new DiagramGenerator(diagrams)
        return (diagramName) => generator.getDiagram(diagramName);
    }

    test('with diagram not found', () => {
        const getDiagram = create([]);
        expect(getDiagram('DefaultDiagram')).toEqual({});
    });

    test('with only name Attribute', () => {
        const diagram = {
            name: 'DefaultDiagram',
        };

        const getDiagram = create([diagram]);
        expect(getDiagram('DefaultDiagram')).toEqual({
            'name':'DefaultDiagram'
        });
    });


    test('with only name and palettes contain no nodes', () => {
        const diagram = {
            name: 'DefaultDiagram',
            palettes: [
                {
                    name: "palette1"
                },
                {
                    name: "palette2"
                }
            ]
        };

        const getDiagram = create([diagram]);
        expect(getDiagram('DefaultDiagram')).toEqual({
            'name':'DefaultDiagram',
            'palettes':[
                {
                    'name':'palette1'
                },
                {
                    'name':'palette2'
                }
            ]
        });
    });


    test('with only name and palettes contain some nodes', () => {
        const diagram = {
            name: 'DefaultDiagram',
            palettes: [
                {
                    name: "palette1",
                    nodes: [
                        "KnotenNode1"
                    ]
                },
                {
                    name: "palette2",
                    nodes: [
                        "KnotenNode2",
                        "KnotenNode3"
                    ]
                }
            ]
        };

        const getDiagram = create([diagram]);
        expect(getDiagram('DefaultDiagram')).toEqual({
            'name':'DefaultDiagram',
            'palettes':[
                {
                    'name':'palette1',
                    'nodes':[
                        'KnotenNode1',
                    ]
                },
                {
                    'name':'palette2',
                    'nodes':[
                        'KnotenNode2',
                        'KnotenNode3',
                    ]
                }
            ]
        });
    });

});