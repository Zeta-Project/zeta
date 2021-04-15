import {
    DefaultPortCandidate,
    FreeNodePortLocationModel,
    IInputModeContext,
    INode,
    List,
    PortCandidateProviderBase,
    PortCandidateValidity
  } from 'yfiles'
  

/** Please refer to https://github.com/yWorks/yfiles-for-html-demos/tree/master/demos/input/portcandidateprovider for implementation details */
export default class NodeCandidateProvider extends PortCandidateProviderBase {

    constructor(node) {
        console.log("creating NodeCadidate")
        super()
        this.node = node
    }
    
    getPortCandidates(context) {
        // TODO:
        // 1. get the current node name
        // 2. get the target of the current node name
        // 3. set item validity based on that

        console.log("context: ");
        console.log(context);

        console.log("node");
        console.log(this.node);

        const candidates = new List()
        const graph = context.graph
        // Create the candidate for each port
        if (graph !== null) {
          this.node.ports.forEach(port => {
            const portCandidate = new DefaultPortCandidate(port)
            portCandidate.validity =
              graph.degree(port) === 0 ? PortCandidateValidity.VALID : PortCandidateValidity.INVALID
            candidates.add(portCandidate)
          })
        }
        // If no candidates have been created so far, create a single invalid candidate as fallback
        if (candidates.size === 0) {
          const item = new DefaultPortCandidate(
            this.node,
            FreeNodePortLocationModel.NODE_CENTER_ANCHORED
          )
          item.validity = PortCandidateValidity.INVALID
          candidates.add(item)
        }
    
        return candidates
      }

}