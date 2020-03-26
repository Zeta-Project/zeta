import {NodeDropInputMode} from "yfiles";

export function configureDndInputMode(graph) {
    const nodeDropInputMode = new NodeDropInputMode()
    nodeDropInputMode.showPreview = true
    nodeDropInputMode.enabled = true
    nodeDropInputMode.isValidParentPredicate = node => {
        return graph.isGroupNode(node)
    }
    return nodeDropInputMode
}