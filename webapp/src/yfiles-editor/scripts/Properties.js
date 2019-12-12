import {
    Fill,
    INode,

} from "yfiles";

import {UMLNodeStyle} from "./UMLNodeStyle";
import {Operation} from "./utils/Operation";

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
     * @param model
     * @param div
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
                /*
                let panel = this.nextElementSibling;
                if (panel.style.maxHeight) {
                    panel.style.maxHeight = null;
                } else {
                    panel.style.maxHeight = panel.scrollHeight + "px";
                }*/
            });
        }
    }
}
function buildOperations(model) {
    let operationList = document.createElement('div')
    operationList.setAttribute("class", "panel")

    if(model.operations === []) return operationList

    //accordion and list setup
    model.tempOperations.forEach((operation) => {
        let openOptionsButton = document.createElement('button')
        openOptionsButton.className = 'accordion'
        openOptionsButton.innerHTML = operation.name
        operationList.appendChild(openOptionsButton)
        let operationInformation = document.createElement('div')
        operationInformation.setAttribute("class", "panel")
        operationList.appendChild(operationInformation)

        //description
        let descriptionLabel = document.createTextNode("Description")
        operationInformation.appendChild(descriptionLabel)
        let description = document.createElement("INPUT");
        description.setAttribute("type", "text");
        description.setAttribute("value", operation.description);
        description.setAttribute("class", "input")
        description.oninput = function(){
            operation.description = description.value
        }
        operationInformation.appendChild(description)

        //parameters
        let parameterList = document.createElement('div')
        parameterList.setAttribute("class", "panel")
        operation.parameters.forEach((parameter) => {
            let openParameterButton = document.createElement('button')
            openParameterButton.className = 'accordion'
            openParameterButton.innerHTML = parameter.value
            parameterList.appendChild(openParameterButton)
            let parameterInformation = document.createElement('div')
            parameterInformation.setAttribute("class", "panel")
            parameterList.appendChild(parameterInformation)

            let parameterText = document.createElement('INPUT')
            parameterText.setAttribute("type", "text");
            parameterText.setAttribute("value", (parameter.value) || "default")
            parameterText.oninput = function(){
                parameter.value = parameterText.value
            }
            parameterInformation.appendChild(parameterText);
            //returnTypeParameter
            let returnTypeLabel = document.createTextNode('returnTypeParameter')
            parameterInformation.appendChild(returnTypeLabel)
            let returnType = document.createElement('SELECT')
            let optString = document.createElement('option')
            optString.text = "String"
            returnType.add(optString)
            let optBool = document.createElement('option')
            optBool.text = "Boolean"
            returnType.add(optBool)
            let optDouble = document.createElement('option')
            optDouble.text = "Double"
            returnType.add(optDouble)
            let optInt = document.createElement('option')
            optInt.text = "Integer"
            returnType.add(optInt)
            parameterInformation.appendChild(returnType)
            //set returnType
            for(let i = 0; i < returnType.options.length; i++){
                if(returnType.options[i].value === parameter.type) returnType.options[i].selected = true
            }
            //set operation.returnType
            returnType.onchange = () => {
                for(let i = 0; i < returnType.options.length; i++){
                    if(returnType.options[i].selected === true) {
                        parameter.type = returnType.options[i].value;
                    }
                }
            }
        })
        operationInformation.appendChild(parameterList)
        let addPropertyButton = document.createElement('button')
        addPropertyButton.innerHTML = "Add Property"
        addPropertyButton.onclick = () => {

        }
        operationInformation.appendChild(addPropertyButton)

        //returnType
        let returnTypeLabel = document.createTextNode('returnType')
        operationInformation.appendChild(returnTypeLabel)
        let returnType = document.createElement('SELECT')
        let optString = document.createElement('option')
        optString.text = "String"
        returnType.add(optString)
        let optBool = document.createElement('option')
        optBool.text = "Boolean"
        returnType.add(optBool)
        let optDouble = document.createElement('option')
        optDouble.text = "Double"
        returnType.add(optDouble)
        let optInt = document.createElement('option')
        optInt.text = "Integer"
        returnType.add(optInt)
        operationInformation.appendChild(returnType)
        //set returnType
        for(let i = 0; i < returnType.options.length; i++){
            if(returnType.options[i].value === operation.returnType) returnType.options[i].selected = true
        }
        //set operation.returnType
        returnType.onchange = () => {
            for(let i = 0; i < returnType.options.length; i++){
                if(returnType.options[i].selected === true) {
                    operation.returnType = returnType.options[i].value;
                }
            }
        }

        //code
        let codeLabel = document.createTextNode("Code")
        operationInformation.appendChild(codeLabel)
        let code = document.createElement("input");
        code.setAttribute("type", "text");
        code.setAttribute("value", operation.code || "");
        code.setAttribute("class", "input")
        code.oninput = function(){
            operation.code = code.value
        }
        operationInformation.appendChild(code)
    })
    let addOperationButton = document.createElement('button')
    addOperationButton.innerHTML = "Add Operation"
    addOperationButton.onclick = () => {
        model.tempOperations.push(new Operation())
    }
    operationList.appendChild(addOperationButton)
    return operationList
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

    if(model.tempAttributes === []) return attributeList

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

        //Default Value
        let defaultVal = document.createElement("INPUT");
        defaultVal.setAttribute("type", "text");
        defaultVal.setAttribute("value", (tempAttribute.defaultVal.value) || "")
        let defaultValLabel = document.createTextNode('default:')
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
        attributeInformation.appendChild(defaultType)
        //set properties default
        for(let i = 0; i < defaultType.options.length; i++){
            if(defaultType.options[i].value === tempAttribute.defaultVal.type) defaultType.options[i].selected = true
        }
        defaultVal.oninput = function(){
            tempAttribute.defaultVal.value = defaultVal.value
        }
        defaultType.onchange = () => {
            for(let i = 0; i < defaultType.options.length; i++){
                if(defaultType.options[i].selected === true) tempAttribute.defaultVal.type = defaultType.options[i].value;
            }
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