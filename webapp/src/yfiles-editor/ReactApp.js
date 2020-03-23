import React, {Component} from "react";
import {YFilesZetaDev} from "./devEnv/app-dev"
import Button from '@material-ui/core/Button';
import DnDPanel from "./components/DnDPanel";
import DemoDataPanel from "./components/DemoDataPanel";
import ReactGraphComponent from "./components/ReactGraphComponent";
import './App.css'

class App extends Component {
    constructor(props) {
        super(props)
        this.state = {
            graphData: {
                nodesSource: [
                    {
                        id: 0,
                        name: 'Node 0'
                    },
                    {
                        id: 1,
                        name: 'Node 1'
                    },
                    {
                        id: 2,
                        name: 'Node 2'
                    }
                ],
                edgesSource: [
                    {
                        fromNode: 0,
                        toNode: 1
                    },
                    {
                        fromNode: 0,
                        toNode: 2
                    }
                ]
            }
        }
    }

    addNode() {
        const newIdx = this.state.graphData.nodesSource.reduce((maxId, item) => Math.max(maxId, item.id), 0) + 1
        const parentNodeIdx = Math.floor(Math.random() * (this.state.graphData.nodesSource.length - 1))
        this.setState(state => {
            const nodesSource = state.graphData.nodesSource.concat({
                id: newIdx,
                name: `Node ${newIdx}`
            })

            // Create an edge if the graph was not empty
            let edgesSource = state.graphData.edgesSource
            if (parentNodeIdx > -1) {
                edgesSource = state.graphData.edgesSource.concat({
                    fromNode: nodesSource[parentNodeIdx].id,
                    toNode: newIdx
                })
            }

            return {
                graphData: {
                    nodesSource,
                    edgesSource
                }
            }
        })
    }

    removeNode() {
        this.setState(state => {
            const randomNodeIdx = Math.floor(Math.random() * (this.state.graphData.nodesSource.length - 1))
            const newNodesSource = [...state.graphData.nodesSource]
            newNodesSource.splice(randomNodeIdx, 1)

            const nodeId = this.state.graphData.nodesSource[randomNodeIdx].id
            const newEdgesSource = state.graphData.edgesSource.filter(
                edge => edge.fromNode !== nodeId && edge.toNode !== nodeId
            )
            return {
                graphData: {
                    nodesSource: newNodesSource,
                    edgesSource: newEdgesSource
                }
            }
        })
    }

    resetData() {
        this.setState({
            graphData: {
                nodesSource: [
                    {
                        id: 0,
                        name: 'Node 0'
                    },
                    {
                        id: 1,
                        name: 'Node 1'
                    },
                    {
                        id: 2,
                        name: 'Node 2'
                    }
                ],
                edgesSource: [
                    {
                        fromNode: 0,
                        toNode: 1
                    },
                    {
                        fromNode: 0,
                        toNode: 2
                    }
                ]
            }
        })
    }

    render() {
        return (
            <div className="App">
                    <ReactGraphComponent
                        graphData={this.state.graphData}
                        onResetData={() => this.resetData()}
                        addNode={() => this.addNode()}
                        removeNode={() => this.removeNode()}
                    />
            </div>
        )
    }
}

export default App;