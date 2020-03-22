import 'yfiles/yfiles.css';

import {bindAction, bindCommand} from "./utils/Bindings";
import {DragAndDrop} from "./layout/dragAndDrop/DragAndDrop";
import * as umlModel from './uml/models/UMLClassModel.js'
import {UMLNodeStyle} from './uml/nodes/styles/UMLNodeStyle.js'
import UMLContextButtonsInputMode from './uml/utils/UMLContextButtonsInputMode.js'
import {isSuccessStatus, ZetaApiWrapper} from "./utils/ZetaApiWrapper";
import {showExportFailure, showSnackbar} from "./utils/Snackbar";
import {
    Class,
    EdgeRouter,
    EdgeRouterScope,
    Fill,
    GraphComponent,
    GraphEditorInputMode,
    GraphSnapContext,
    GridSnapTypes,
    HierarchicLayout,
    HierarchicLayoutData,
    ICommand, IEdge,
    INode,
    LabelSnapContext,
    LayoutExecutor,
    License,
    List,
    OrthogonalEdgeEditingContext,
    PolylineEdgeRouterData,
    Size
} from 'yfiles'
import {PropertyPanel} from "./layout/propertyPanel/PropertyPanel";
import Exporter from "./export/Exporter"

import './styles/layout.css'
import './styles/paper.css'
import './styles/stencil.css'
import './styles/style.css'
import './styles/toolbar.css'
import './styles/sidebar.css'
import {Attribute} from "./uml/attributes/Attribute";
import {Operation} from "./uml/operations/Operation";
import {Parameter} from "./uml/parameters/Parameter";
import {UMLEdgeStyle} from './uml/edges/styles/UMLEdgeStyle'
import * as umlEdgeModel from './uml/edges/UMLEdgeModel'
import {
    createAggregationStyle,
    createAssociationStyle,
    createCompositionStyle,
    createGeneralizationStyle
} from "./uml/edges/styles/UMLEdgeStyleFactory";
import {buildGraphFromDefinition, executeLayout, getInputMode, registerCommands, setDefaultStyles} from "./uml/utils/GraphComponentUtils";
import {Grid} from "./layout/grid/Grid";


// Tell the library about the license contents
License.value = require('../../../../yfiles-for-html/lib/license.json');

// We need to load the yfiles/view-layout-bridge module explicitly to prevent the webpack
// tree shaker from removing this dependency which is needed for 'morphLayout' in this demo.
Class.ensure(LayoutExecutor);


/**
 * A simple yFiles application that creates a GraphComponent and enables basic input gestures.
 */

//move graph inside class YFilesZeta?
let graphComponent = null;

export class YFilesZeta {

    constructor(loadedMetaModel) {
        this.loadedMetaModel = loadedMetaModel;
        this.initialize();
    }

    initialize() {
        graphComponent = new GraphComponent('#graphComponent');
        const graph = graphComponent.graph;
        graph.undoEngineEnabled = true
        graphComponent.inputMode = getInputMode(graphComponent)

        // configures default styles for newly created graph elements
        setDefaultStyles(graphComponent)


        if (this.loadedMetaModel.constructor === Object && Object.entries(this.loadedMetaModel).length > 0 && Object.entries(this.loadedMetaModel.concept).length > 0) {
            buildGraphFromDefinition(graphComponent, this.loadedMetaModel.concept);

            executeLayout(graphComponent).then(() => {
                // the graph bootstrapping should not be undoable
                graphComponent.graph.undoEngine.clear()
            }).then(error => console.error(error))
        } else {
            showSnackbar("No loaded meta model found");
        }

        // configure and initialize drag and drop panel
        let dragAndDropPanel = new DragAndDrop(graphComponent);
        let propertyPanel = new PropertyPanel(graphComponent);

        //Question: Why does this work but the bottom one doesn't? -> graphComponent.selection.addItemSelectionChangedListener(propertyPanel.updateProperties)
        graphComponent.selection.addItemSelectionChangedListener((src, args) => {
            //if (INode.isInstance(args.item) && args.item.style instanceof UMLNodeStyle)
            propertyPanel.updateProperties(src, args)
        });

        graphComponent.fitGraphBounds();

        // Init grid
        const grid = new Grid(graphComponent)
        // Set the default snapping behaviour
        grid.initializeSnapping()
        grid.initializeGrid()
        grid.registerCommand()
        graphComponent = grid.graphComponent

        // bind toolbar commands
        registerCommands(graphComponent, this.loadedMetaModel)
    }
}

