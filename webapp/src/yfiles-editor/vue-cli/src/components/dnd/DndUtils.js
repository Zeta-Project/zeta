import {NodeDropInputMode} from "yfiles";

/**
 * Returns the zeta default input mode for dnd actions
 * @param graph
 * @returns {NodeDropInputMode}
 */
export function getDefaultDndInputMode(graph) {
    const nodeDropInputMode = new NodeDropInputMode()
    nodeDropInputMode.showPreview = true
    nodeDropInputMode.enabled = true
    nodeDropInputMode.isValidParentPredicate = node => {
        return graph.isGroupNode(node)
    }
    return nodeDropInputMode
}