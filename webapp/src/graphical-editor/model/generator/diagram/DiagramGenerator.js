class DiagramGenerator {
    generate(diagram) {
        return Object.assign(
            this.createName(diagram),
            this.createPalettes(diagram)
        );
    }

    createName(diagram) {
        return {
            'name': diagram.name
        };
    }

    createPalettes(diagram) {
        return {
            'palettes': diagram.palettes
        };
    }

}

// ToDo: in old logic palette is get by Nodes (example: you get  palettes by an array of nodes)
// ToDo: Nodes are handles only in Shape? or get Node
class PaletteGenerator{

}

/**
 * Generator of diagram information for JointJS
 */
export default class Generator {

    constructor(diagrams) {
        this.diagrams = diagrams;
        this.diagramGenerator = new DiagramGenerator();
    }

    getDiagram(diagramName) {
        const diagram = this.diagrams.find((d) => d.name === diagramName);
        return diagram ? this.diagramGenerator.generate(diagram) : {};
    }
}