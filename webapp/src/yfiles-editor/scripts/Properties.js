import {
    INode,

} from "yfiles";

import {UMLNodeStyle} from "./UMLNodeStyle";

export class Properties {

    constructor(graphComponent) {
        // retrieve the panel element
        this.divField = document.getElementById('properties-panel')
        this.currentItem = graphComponent.graph.currentItem
        this.graphComponent = graphComponent
    }

    get div() {
        return this.divField
    }
    set div(div) {
        this.divField = div
    }


    /**
     * gets called upon selecting another ui element
     * @param sender
     * @param args
     */
    updateProperties(sender, args) {
        let div = document.getElementById('properties-panel')
        //let div = this.div
        if (args == null) return
        let item = args.item
        let model = item.style.model

        if (INode.isInstance(item) && item.style instanceof UMLNodeStyle) {
            //There is a node and it is type of UMLNodeStyle
            this.div.innerHTML = ""
            this.buildNodeProperties(model, div)
        }
        else {
            this.div.innerHTML = ""
            console.log("No Valid Item Selected")
        }
    }

    buildNodeProperties(model, div) {

        //build metaAccordion
        let accordionMeta = document.createElement('button')
        accordionMeta.className = 'accordion'
        accordionMeta.innerHTML = 'MetaInformation'
        div.appendChild(accordionMeta)

        //add MetaPanel
        let pMeta = this.buildMeta(model)
        div.appendChild(pMeta)

        //build attributeAccordion
        let accordionAttributes = document.createElement('button')
        accordionAttributes.className = 'accordion'
        accordionAttributes.innerHTML = 'Attributes'
        div.appendChild(accordionAttributes)

        //build AttributePanel
        div.appendChild(this.buildAttributes(model))

        //build operationAccordion
        let accordionOperations = document.createElement('button')
        accordionOperations.className = 'accordion'
        accordionOperations.innerHTML = 'Operations'
        div.appendChild(accordionOperations)

        // build OperationPanel
        div.appendChild(this.buildOperations(model))

    }

    buildMeta(model) {
        let pMeta = document.createElement('p')
        pMeta.class = 'panel'

        //name
        let nameLabel = document.createTextNode("Name")
        pMeta.appendChild(nameLabel)
        let name = document.createElement("INPUT");
        name.setAttribute("type", "text");
        name.setAttribute("value", model.className)
        name.class = "input"
        name.oninput = function(){
            model.className = name.value
        }
        pMeta.appendChild(name)

        //description
        let descriptionLabel = document.createTextNode("Description")
        pMeta.appendChild(descriptionLabel)
        let description = document.createElement("INPUT");
        description.setAttribute("type", "text");
        description.setAttribute("value", model.description);
        description.class = "input"
        description.oninput = function(){
            model.description = description.value
        }
        pMeta.appendChild(description)

        //abstractness
        //Todo add neat icons like in UML
        let abstractLabel = document.createTextNode("Abstract")
        pMeta.appendChild(abstractLabel)
        let abstractness = document.createElement("INPUT")
        abstractness.setAttribute("type", "checkbox")
        abstractness.setAttribute("value", model.abstract)
        abstractness.onchange = function() {
            if(abstractness === true) {
                abstractness.value = false
            } else {
                abstractness.value = true
            }
            model.abstract = abstractness.value
            console.log(abstractness.value)
        }
        pMeta.appendChild(abstractness)
        return pMeta
    }

    buildAttributes(model) {
        let pAttributes = document.createElement('p')
        pAttributes.class = 'panel'
        //label
        let attributesLabel = document.createTextNode("Attributes")
        pAttributes.appendChild(attributesLabel)
        //for each attribute
        let attributes = []
        for(let i = 0; i < model.attributes.length; i++) {
            attributes.push(model.attributes[i])
            let textBox = document.createElement("INPUT");
            textBox.setAttribute("type", "text");
            textBox.setAttribute("value", model.attributes[i].name)
            textBox.oninput = function(){
                model.attributes[i] = textBox.value
            }
            pAttributes.appendChild(textBox);
            //add relevant checkboxes
            let localUniqueBox = document.createElement("input")
            localUniqueBox.type = "checkbox"
            localUniqueBox.text = "LocalUnique"
            localUniqueBox.onchange = function() {
                if(localUniqueBox.checked) model.attributes[i].localUnique
            }
            pAttributes.appendChild(localUniqueBox);
        }
        return pAttributes
    }

    buildOperations(model) {
        let pOperations = document.createElement('p')
        pOperations.class = 'panel'
        //label
        let operationsLabel = document.createTextNode("Operations")
        pOperations.appendChild(operationsLabel)
        for(let i = 0; i < model.operations.length; i++) {
            let textBox = document.createElement("INPUT");
            textBox.setAttribute("type", "text");
            textBox.setAttribute("value", model.operations[i].toString())
            textBox.oninput = function(){
                model.operations[i] = textBox.value
            }
            pOperations.appendChild(textBox);
        }
        return pOperations
    }

    buildEdgeProperties() {
    }
}