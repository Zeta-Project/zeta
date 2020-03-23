import React, {Component} from 'react'
import reloadIcon from '../assets/reload-16.svg'
import './DemoDescription.css'
import {DragAndDropPanel} from "../layout/dragAndDrop/Panel";
import {passiveSupported} from "../utils/Workarounds";
import {
    DragDropEffects,
    DragDropItem,
    DragSource,
    GraphComponent, GraphViewerInputMode,
    IEdge,
    ILabel,
    IListEnumerable,
    INode,
    Insets,
    IPort,
    IStripe,
    LabelDropInputMode, License, ListEnumerable,
    NodeDropInputMode, PanelNodeStyle,
    Point,
    PortDropInputMode,
    Rect,
    SimpleNode,
    SvgExport,
    VoidNodeStyle
} from "yfiles";
import {addClass, removeClass} from "../utils/Bindings";
import {createDnDPanelItems, DragAndDrop} from "../layout/dragAndDrop/DragAndDrop";

export default class DnDPanel extends Component {
    constructor(props) {
        super(props)
        this.state = {
            passiveSupported: true,
            maxItemWidth: 100,
            $copyNodeLabels: true
        }
    }

    componentDidMount() {
        let dragAndDropPanel = new DragAndDrop(this.props.graphComponent);
    }


    render() {
        return (
            <div>
                <h1 className="demo-sidebar-header">Description</h1>
                <div className="demo-sidebar-content">
                    <div id="drag-and-drop-panel"/>
                </div>
            </div>
        )
    }
}
