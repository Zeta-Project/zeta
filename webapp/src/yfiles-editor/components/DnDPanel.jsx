import React, {Component} from 'react'
import './DnDPanel.css'
import {DragAndDrop} from "../layout/dragAndDrop/DragAndDrop";

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
        new DragAndDrop(this.props.graphComponent);
    }

    render() {
        return (
            <div>
                <h1 className="demo-sidebar-header">Drag and Drop</h1>
                <div className="demo-sidebar-content">
                    <div id="drag-and-drop-panel"/>
                </div>
            </div>
        )
    }
}
