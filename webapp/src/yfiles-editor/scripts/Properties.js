import {
    INode,

} from "yfiles";

import {UMLNodeStyle} from "./UMLNodeStyle";

export class Properties {

    /**
     *
     * @param graphComponent
     */
    constructor(graphComponent) {
        // retrieve the panel element
        this.divField = document.getElementById('properties-panel')
        //this.graphComponent = graphComponent
        this.itemSelectionChangedListener = (sender, args) => this.itemSelectionChanged(sender, args)
        graphComponent.selection.addItemSelectionChangedListener(this.itemSelectionChangedListener)

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
    itemSelectionChanged(sender, args) {
        if (args == null) return
        let item = args.item
        let model = item.style.model

        if (INode.isInstance(item) && item.style instanceof UMLNodeStyle) {
            //There is a node and it is type of UMLNodeStyle
            if(this.div.childNodes.length > 0) {
                //this.updateProperties(model, this.div)
                //Todo check if update instead of rebuild is necessary
                this.div.innerHTML = ""
                this.buildNodeProperties(model, this.div)
            } else {
                this.div.innerHTML = ""
                this.buildNodeProperties(model, this.div)
            }
        }
        else {
            this.div.innerHTML = ""
            console.log("No Valid Item Selected")
        }
    }

    /**
     * Todo update panel content instead of rebuilding it - necessary?
     * @param sender
     * @param args
     */
    updateProperties(model, div) {
        console.log(this)
        console.log(div)
    }

    buildNodeProperties(model, div) {

        //build metaAccordion
        let accordionMeta = document.createElement('button')
        accordionMeta.className = 'accordion'
        accordionMeta.innerHTML = 'MetaInformation'
        div.appendChild(accordionMeta)

        //add MetaPanel
        div.appendChild(buildMeta(model))

        //build attributeAccordion
        let accordionAttributes = document.createElement('button')
        accordionAttributes.className = 'accordion'
        accordionAttributes.innerHTML = 'Attributes'
        div.appendChild(accordionAttributes)

        //build AttributePanel
        div.appendChild(buildAttributes(model))

        //build operationAccordion
        let accordionOperations = document.createElement('button')
        accordionOperations.className = 'accordion'
        accordionOperations.innerHTML = 'Operations'
        div.appendChild(accordionOperations)

        // build OperationPanel
        div.appendChild(buildOperations(model))

        let acc = document.getElementsByClassName('accordion');
        let i;
        for (i = 0; i < acc.length; i++) {
            acc[i].addEventListener("click", function() {
                this.classList.toggle("active");
                let panel = this.nextElementSibling;
                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null;
                } else {
                    panel.style.maxHeight = panel.scrollHeight + "px";
                }
            });
        }

    }
}


function buildMeta(model) {

    let pMeta = document.createElement('div')
    pMeta.setAttribute("class", "panel")

    //name
    let nameLabel = document.createTextNode("Name")
    pMeta.appendChild(nameLabel)
    let name = document.createElement("INPUT");
    name.setAttribute("type", "text");
    name.setAttribute("value", model.className)
    name.setAttribute("class", "input")
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
    description.setAttribute("class", "input")
    description.oninput = function(){
        model.description = description.value
    }
    pMeta.appendChild(description)

    //isAbstract
    //Todo add neat icons like in UMLNode example
    let abstractLabel = document.createTextNode("Abstract")
    pMeta.appendChild(abstractLabel)
    let isAbstract = document.createElement("INPUT")
    isAbstract.setAttribute("type", "checkbox")
    isAbstract.setAttribute("value", model.abstract)
    isAbstract.onchange = function() {
        if(isAbstract === true) {
            isAbstract.value = false
        } else {
            isAbstract.value = true
        }
        model.abstract = isAbstract.value
    }
    pMeta.appendChild(isAbstract)
    return pMeta
}

function buildAttributes(model) {
    let attributeList = document.createElement('div')
    attributeList.setAttribute("class", "panel")

    model.tempAttributes.forEach((tempAttribute) => {
        let openAttributeButton = document.createElement('button')
        openAttributeButton.className = 'accordion'
        openAttributeButton.innerHTML = tempAttribute.name
        attributeList.appendChild(openAttributeButton)
        let attributeInformation = document.createElement('div')
        attributeInformation.setAttribute("class", "panel")
        attributeList.appendChild(attributeInformation)

        //upper Bound
        let upperBound = document.createElement("INPUT");
        upperBound.setAttribute("type", "text");
        upperBound.setAttribute("value", (tempAttribute.upperBound) || "")
        upperBound.oninput = function(){
            tempAttribute.upperBound = upperBound.value
        }
        let upperBoundLabel = document.createTextNode('Upper Bound:')
        attributeInformation.appendChild(upperBoundLabel);
        attributeInformation.appendChild(upperBound);

        //lower Bound
        let lowerBound = document.createElement("INPUT");
        lowerBound.setAttribute("type", "text");
        lowerBound.setAttribute("value", (tempAttribute.lowerBound) || "")
        lowerBound.oninput = function(){
            tempAttribute.lowerBound = lowerBound.value
        }
        let lowerBoundLabel = document.createTextNode('Lower Bound:')
        attributeInformation.appendChild(lowerBoundLabel);
        attributeInformation.appendChild(lowerBound);

        //Default - Type(Auswahl)
        let defaultVal = document.createElement("INPUT");
        defaultVal.setAttribute("type", "text");
        defaultVal.setAttribute("value", (tempAttribute.defaultVal.value) || "")
        let defaultValLabel = document.createTextNode('defaultVal:')
        attributeInformation.appendChild(defaultValLabel);
        attributeInformation.appendChild(defaultVal);
        //DefaultType Dropdown
        let defaultType = document.createElement('SELECT')
        let optString = document.createElement('option')
        optString.text = "String"
        defaultType.add(optString)
        let optBool = document.createElement('option')
        optBool.text = "Boolean"
        defaultType.add(optBool)
        let optDouble = document.createElement('option')
        optDouble.text = "Double"
        defaultType.add(optDouble)
        let optInt = document.createElement('option')
        optInt.text = "Integer"
        defaultType.add(optInt)
        //Todo Switch case für Type einbinden
        //defaultType.setAttribute('option', tempAttribute.defaultVal.value)
        defaultType.value = tempAttribute.defaultVal.value
        attributeInformation.appendChild(defaultType)
        //oninput is last to assure type is added
        defaultVal.oninput = function(){
            tempAttribute.defaultVal.value = defaultVal.value
        }
        defaultType.oninput = () => {
            tempAttribute.defaultVal.type = defaultType.value
            console.log(tempAttribute.defaultVal.type)
            console.log(tempAttribute.defaultVal.value)

        }

        //Expression
        let expression = document.createElement("INPUT");
        expression.setAttribute("type", "text");
        expression.setAttribute("value", (tempAttribute.expression) || "")
        expression.oninput = function(){
            tempAttribute.expression = expression.value
        }
        let expressionLabel = document.createTextNode('expression:')
        attributeInformation.appendChild(expressionLabel);
        attributeInformation.appendChild(expression);

        //Todo create checkBoxBuild function
        //globalUnique
        let globalUnique = document.createElement('input')
        globalUnique.setAttribute("type", "checkbox")
        if(tempAttribute.globalUnique) globalUnique.checked = true
        globalUnique.setAttribute("name", "globalUnique")
        globalUnique.onchange = () => {
            tempAttribute.globalUnique = globalUnique.checked
            console.log(tempAttribute.globalUnique)
        }
        let globalUniqueLabel = document.createTextNode("globalUnique")
        attributeInformation.appendChild(globalUniqueLabel)
        attributeInformation.appendChild(globalUnique)

        //localUnique
        let localUnique = document.createElement('input')
        localUnique.setAttribute("type", "checkbox")
        if(tempAttribute.localUnique) localUnique.checked = true
        localUnique.setAttribute("name", "localUnique")
        localUnique.onchange = () => {
            tempAttribute.localUnique = localUnique.checked
            console.log(tempAttribute.localUnique)
        }
        let localUniqueLabel = document.createTextNode("localUnique")
        attributeInformation.appendChild(localUniqueLabel)
        attributeInformation.appendChild(localUnique)

        //constant
        let constant = document.createElement('input')
        constant.setAttribute("type", "checkbox")
        if(tempAttribute.constant) constant.checked = true
        constant.setAttribute("name", "constant")
        constant.onchange = () => {
            tempAttribute.constant = constant.checked
            console.log(tempAttribute.constant)
        }
        let constantLabel = document.createTextNode("localUnique")
        attributeInformation.appendChild(constantLabel)
        attributeInformation.appendChild(constant)

        //ordered
        let ordered = document.createElement('input')
        ordered.setAttribute("type", "checkbox")
        if(tempAttribute.ordered) ordered.checked = true
        ordered.setAttribute("name", "ordered")
        ordered.onchange = () => {
            tempAttribute.ordered = ordered.checked
            console.log(tempAttribute.ordered)
        }
        let orderedLabel = document.createTextNode("ordered")
        attributeInformation.appendChild(orderedLabel)
        attributeInformation.appendChild(ordered)

        //singleAssignment
        let singleAssignment = document.createElement('input')
        singleAssignment.setAttribute("type", "checkbox")
        if(tempAttribute.singleAssignment) singleAssignment.checked = true
        singleAssignment.setAttribute("name", "singleAssignment")
        singleAssignment.onchange = () => {
            tempAttribute.singleAssignment = singleAssignment.checked
            console.log(tempAttribute.singleAssignment)
        }
        let singleAssignmentLabel = document.createTextNode("singleAssignment")
        attributeInformation.appendChild(singleAssignmentLabel)
        attributeInformation.appendChild(singleAssignment)

        //transient
        let transient = document.createElement('input')
        transient.setAttribute("type", "checkbox")
        if(tempAttribute.singleAssignment) transient.checked = true
        transient.setAttribute("name", "transient")
        transient.onchange = () => {
            tempAttribute.transient = transient.checked
            console.log(tempAttribute.transient)
        }
        let transientLabel = document.createTextNode("transient")
        attributeInformation.appendChild(transientLabel)
        attributeInformation.appendChild(transient)

    })
    return attributeList

}

function buildOperations(model) {
    let pOperations = document.createElement('div')
    pOperations.setAttribute("class", "panel")
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