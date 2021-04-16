import {
    DefaultPortCandidate,
    FreeNodePortLocationModel,
    List,
    PortCandidateProviderBase,
    PortCandidateValidity
} from 'yfiles'


/** Please refer to https://github.com/yWorks/yfiles-for-html-demos/tree/master/demos/input/portcandidateprovider for implementation details */
export default class NodeCandidateProvider extends PortCandidateProviderBase {

    constructor(node, target) {
        super()
        this.node = node
        this.target = target
    }

    getPortCandidates(context) {
        const candidates = new List()
        const graph = context.graph
        // Create the candidate for each port
        if (graph !== null) {
            const item = new DefaultPortCandidate(
                this.node,
                FreeNodePortLocationModel.NODE_CENTER_ANCHORED
            )
            item.validity = this.node.tag.className == this.target ? PortCandidateValidity.VALID : PortCandidateValidity.INVALID

            candidates.add(item)
        }
        return candidates
    }
}