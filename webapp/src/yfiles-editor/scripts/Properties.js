import {
    INode,

} from "yfiles";

import {UMLNodeStyle} from "./UMLNodeStyle";

export class Properties {

    constructor(graphComponent) {
        // retrieve the panel element
        this.divField = document.getElementById('properties-panel')

        this.graphComponent = graphComponent
        //this.graphComponent.selection.addItemSelectionChangedListener(this.updateProperties)
        //console.log(this.graphComponent)
    }

    get div() {
        return this.divField
    }
    set div(div) {
        this.divField = div
    }

    updateProperties(sender, args) {
        let div = this.div
        if (args == null) return
        let item = args.item
        let model = item.style.model

        if (INode.isInstance(item) && item.style instanceof UMLNodeStyle) {
            //There is a node and it is type of UMLNodeStyle
            if (!div.hasChildNodes()) {
                //update properties
            }
            //rebuild properties
            this.div.innerHTML = ""
            this.div = this.buildUMLNodeProperties(model, div) //not sure where to set object.div

            //this.div = this.updateUMLNodeProperties(model, div)
        }
        else {
            this.div.removeAll()
            console.log(item)
        }
    }


    buildUMLNodeProperties(model, container) {
        let div = container
        //build metapanel
        let accordionMeta = document.createElement('button')
        accordionMeta.className = 'accordion'
        accordionMeta.innerHTML = 'MetaInformation'
        let pMeta = document.createElement('p')
        pMeta.class = 'panel'

        //name
        let nameLabel = document.createTextNode("Name")
        pMeta.appendChild(nameLabel)
        let name = document.createElement("INPUT");
        name.setAttribute("type", "text");
        name.setAttribute("value", model.className);
        name.class = "input"
        name.oninput = function(){
            console.log(model.className = name.value)
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
            console.log(model.description = description.value)
        }
        pMeta.appendChild(description)

        //abstractness
        //Todo add beautiful icons like in UML

        //attributespanel
        let accordionAttributes = document.createElement('button')
        accordionAttributes.className = 'accordion'
        accordionAttributes.innerHTML = 'Attributes'
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

        //pAttributes.className = model.attributes.toString()

        //operationspanel
        let accordionOperations = document.createElement('button')
        accordionOperations.className = 'accordion'
        accordionOperations.innerHTML = 'Operations'
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


        div.appendChild(accordionMeta)
        div.appendChild(pMeta)
        div.appendChild(accordionAttributes)
        div.appendChild(pAttributes)
        div.appendChild(accordionOperations)
        div.appendChild(pOperations)

        let acc = document.getElementsByClassName('accordion'); //cant find Elements
        for (let i = 0; i < acc.length; i++) {
            acc[i].addEventListener("click", function () {
                /* Toggle between adding and removing the "active" class,
                to highlight the button that controls the panel */
                this.classList.toggle("active");

                /* Toggle between hiding and showing the active panel */
                let panel = this.nextElementSibling;
                if (panel.style.display === "block") {
                    panel.style.display = "none";
                } else {
                    panel.style.display = "block";
                }
            });
        }
        return div
    }
}