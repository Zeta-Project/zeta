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
            //console.log("No Valid Item Selected")
        }
    }

    /**
     * Todo update panel content instead of rebuilding it - necessary?
     * @param model
     * @param div
     */
    updateProperties(model, div) {
        //console.log(this)
        //console.log(div)
    }

    buildNodeProperties(model, div) {

        //build metaAccordion
        let accordionMeta = document.createElement('button')
        accordionMeta.className = 'collapsible'
        accordionMeta.innerHTML = 'MetaInformation'
        div.appendChild(accordionMeta)

        //add MetaPanel
        div.appendChild(buildMeta(model))

        //build attributeAccordion
        let accordionAttributes = document.createElement('button')
        accordionAttributes.className = 'collapsible'
        accordionAttributes.innerHTML = 'Attributes'
        div.appendChild(accordionAttributes)

        //build AttributePanel
        div.appendChild(buildAttributes(model))

        //build operationAccordion
        let accordionOperations = document.createElement('button')
        accordionOperations.className = 'collapsible'
        accordionOperations.innerHTML = 'Operations'
        div.appendChild(accordionOperations)

        //build OperationPanel
        div.appendChild(buildOperations(model))

        let coll = document.getElementsByClassName("collapsible");
        let i;

        for (i = 0; i < coll.length; i++) {
            coll[i].addEventListener("click", function() {
                this.classList.toggle("active");
                let content = this.nextElementSibling;
                if (content.style.maxHeight){
                    content.style.maxHeight = null;
                } else {
                    content.style.maxHeight = content.scrollHeight + "px";

                }
            });
        }
    }
}

function buildMeta(model) {
    let metaContainer = document.createElement('DIV')
    metaContainer.setAttribute("class", "collapsibleContent")

    //name
    let nameLabel = document.createTextNode("Name")
    metaContainer.appendChild(nameLabel)
    let name = document.createElement("INPUT");
    name.setAttribute("type", "text");
    name.setAttribute("value", model.className)
    name.setAttribute("class", "input")
    name.oninput = function(){
        model.className = name.value
    }
    metaContainer.appendChild(name)

    //description
    let descriptionLabel = document.createTextNode("Description")
    metaContainer.appendChild(descriptionLabel)
    let description = document.createElement("INPUT");
    description.setAttribute("type", "text");
    description.setAttribute("value", model.description);
    description.setAttribute("class", "input")
    description.oninput = function(){
        model.description = description.value
    }
    metaContainer.appendChild(description)

    //isAbstract
    let abstractLabel = document.createTextNode("Abstract")
    metaContainer.appendChild(abstractLabel)
    let isAbstract = document.createElement("INPUT")
    isAbstract.setAttribute("type", "checkbox")
    if(model.abstract) isAbstract.checked = true;
    isAbstract.onchange = function() {
        if(isAbstract.checked) {
            model.abstract = true
        } else {
            model.abstract = false
        }
    }
    metaContainer.appendChild(isAbstract)
    return metaContainer
}

function buildAttribute(model ,attribute) {

    let singleAttribute = document.createElement('div')
    singleAttribute.className = 'singleAttributeContainer'

    let attributeName = document.createElement('INPUT')
    attributeName.setAttribute('value', attribute.name)
    attributeName.className = 'elementName';
    singleAttribute.appendChild(attributeName);

    let removeAttributeButton = document.createElement('button')
    removeAttributeButton.className = 'removeElementButton'
    singleAttribute.appendChild(removeAttributeButton)
    removeAttributeButton.onclick = function(e) {
        if (confirm('Attribut entfernen?')) {
            this.parentNode.parentNode.removeChild(this.parentNode)
            let index = model.attributes.indexOf(attribute)
            model.attributes.splice(index, 1)
        } else {
            // Do nothing!
        }
    }

    let attributeListButton = document.createElement('button')
    attributeListButton.className = 'listCollapsibleButton'
    attributeListButton.onclick = function() {
        attributeListButton.classList.toggle('listCollapsibleButtonOpen')
        attributeListButton.classList.toggle('listCollapsibleButton')
        let content = this.nextElementSibling;
        if (content.style.maxHeight){
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
        }
    }
    singleAttribute.appendChild(attributeListButton)

    //collapsible Details
    let singleAttributeDetails = document.createElement('div')
    singleAttributeDetails.className = 'collapsibleContent'
    singleAttribute.appendChild(singleAttributeDetails)

    //upper Bound
    singleAttributeDetails.appendChild(buildTextBox("upperBound", attribute, "upperBound"))

    //lower Bound
    singleAttributeDetails.appendChild(buildTextBox("lowerBound", attribute, "lowerBound"))

    //Default Value
    let defaultValue = document.createElement("INPUT");
    defaultValue.setAttribute("type", "text");
    defaultValue.className = 'textBox'
    defaultValue.setAttribute("value", (attribute.default.value) || "")
    let defaultValLabel = document.createTextNode('default:')
    singleAttributeDetails.appendChild(defaultValLabel);
    singleAttributeDetails.appendChild(defaultValue);

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
    singleAttributeDetails.appendChild(defaultType)
    //set properties default
    for(let i = 0; i < defaultType.options.length; i++){
        if(defaultType.options[i].value === attribute.default.type) defaultType.options[i].selected = true
    }
    defaultValue.oninput = function(){
        attribute.default.value = defaultValue.value
    }
    defaultType.onchange = () => {
        for(let i = 0; i < defaultType.options.length; i++){
            if(defaultType.options[i].selected === true) attribute.default.type = defaultType.options[i].value;
        }
    }
    //Expression
    singleAttributeDetails.appendChild(buildTextBox("expression", attribute, "expression"))

    //Checkboxes
    singleAttributeDetails.appendChild(buildCheckBox("globalUnique", attribute, "globalUnique"))
    singleAttributeDetails.appendChild(buildCheckBox("localUnique", attribute, "localUnique"))
    singleAttributeDetails.appendChild(buildCheckBox("constant", attribute, "constant"))
    singleAttributeDetails.appendChild(buildCheckBox("ordered", attribute, "ordered"))
    singleAttributeDetails.appendChild(buildCheckBox("singleAssignment", attribute, "singleAssignment"))
    singleAttributeDetails.appendChild(buildCheckBox("transient", attribute, "transient"))

    return singleAttribute
}

function buildTextBox(label, data, value) {
    let textBoxContainer = document.createElement('div');
    textBoxContainer.className = 'textBoxContainer';
    let textBox = document.createElement("input");
    textBox.className = 'textBox';
    textBox.setAttribute("type", "text");
    textBox.setAttribute("value", data[value] || "");
    textBox.oninput = function() {
        data[value] = textBox.value
    };
    let textBoxLabel = document.createTextNode(label);
    textBoxContainer.appendChild(textBoxLabel);
    textBoxContainer.appendChild(textBox);
    return textBoxContainer;
}

function buildCheckBox(label, data, boolAttribute) {
    //globalUnique
    let checkBoxContainer = document.createElement('div');
    let checkbox = document.createElement('input')
    checkbox.setAttribute("type", "checkbox");
    checkBoxContainer.className = 'checkbox';
    if(data[boolAttribute]) checkbox.checked = true;
    checkbox.onchange = () => {
        data[boolAttribute] = checkbox.checked
        //console.log(attribute.globalUnique)
    }
    let checkboxLabel = document.createTextNode(label);
    checkBoxContainer.appendChild(checkbox);
    checkBoxContainer.appendChild(checkboxLabel);
    return checkBoxContainer
}

function buildAttributes(model) {
    let attributeContainer = document.createElement('div');
    attributeContainer.setAttribute("class", "collapsibleContent");

    //return of no attributes in node
    if(model.attributes === []) return attributeContainer
    model.attributes.forEach((attribute) => {
        attributeContainer.appendChild(buildAttribute(model, attribute))
    });
    return attributeContainer
}

function buildParameter(operation, parameter) {
    let singleParameter = document.createElement('div');
    singleParameter.className = 'singleParameterContainer';

    let paraMeterName = document.createElement('INPUT');
    paraMeterName.setAttribute('value', parameter.value);
    paraMeterName.className = 'elementName';
    singleParameter.appendChild(paraMeterName);

    let removeParameterButton = document.createElement('button');
    removeParameterButton.className = 'removeElementButton';
    singleParameter.appendChild(removeParameterButton);
    removeParameterButton.onclick = function(e) {
        if (confirm('Parameter entfernen?')) {
            this.parentNode.parentNode.removeChild(this.parentNode);
            let index = operation.parameters.indexOf(parameter);
            operation.parameters.splice(index, 1)
        }
    }

    let parameterListButton = document.createElement('button')
    parameterListButton.className = 'listCollapsibleButton'
    parameterListButton.onclick = function() {
        parameterListButton.classList.toggle('listCollapsibleButtonOpen')
        parameterListButton.classList.toggle('listCollapsibleButton')
        let content = this.nextElementSibling;
        if (content.style.maxHeight){
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
        }
    }
    singleParameter.appendChild(parameterListButton)

    let parameterDetails = document.createElement('div')
    parameterDetails.className = 'collapsibleContent'
    singleParameter.appendChild(parameterDetails)

    //returnTypeParameter
    let returnTypeLabel = document.createTextNode('returnTypeParameter')
    parameterDetails.appendChild(returnTypeLabel)
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
    parameterDetails.appendChild(returnType)
    //set returnType
    for(let i = 0; i < returnType.options.length; i++){
        //console.log(parameter.type)
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

    return singleParameter;
}

function buildOperations(model) {
    let operationContainer = document.createElement('div')
    operationContainer.setAttribute("class", "collapsibleContent")

    if(model.operations === []) return operationContainer
    model.operations.forEach((operation) => {
        operationContainer.appendChild(buildOperation(model, operation))
    });
    return operationContainer
}

function buildOperation(model, operation) {
    let singleOperation = document.createElement('div')
    singleOperation.className = 'singleOperationContainer'

    let operationName = document.createElement('INPUT')
    operationName.setAttribute('value', operation.name)
    operationName.className = 'elementName';
    singleOperation.appendChild(operationName);

    let removeOperationButton = document.createElement('button')
    removeOperationButton.className = 'removeElementButton'
    singleOperation.appendChild(removeOperationButton)
    removeOperationButton.onclick = function(e) {
        if (confirm('Operation entfernen?')) {
            this.parentNode.parentNode.removeChild(this.parentNode)
            let index = model.operations.indexOf(operation)
            model.operations.splice(index, 1)
        } else {
            // Do nothing!
        }
    }

    let operationListButton = document.createElement('button')
    operationListButton.className = 'listCollapsibleButton'
    operationListButton.onclick = function() {
        operationListButton.classList.toggle('listCollapsibleButtonOpen')
        operationListButton.classList.toggle('listCollapsibleButton')
        let content = this.nextElementSibling;
        if (content.style.maxHeight){
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
        }
    }
    singleOperation.appendChild(operationListButton)

    //collapsible Details
    let singleOperationDetails = document.createElement('div')
    singleOperationDetails.className = 'collapsibleContent'
    singleOperation.appendChild(singleOperationDetails)

    //description
    singleOperationDetails.appendChild(buildTextBox("description", operation, "description"))


    //Parameterlist
    let parameterContainer = document.createElement('div')
    parameterContainer.setAttribute("class", "collapsibleContent")

    operation.parameters.forEach((parameter) => {
        //parameterContainer.appendChild(buildParameter(parameter))
        singleOperationDetails.appendChild(buildParameter(operation, parameter))
    });

    let addPropertyButton = document.createElement('button')
    addPropertyButton.innerHTML = "Add Parameter"
    addPropertyButton.onclick = () => {
        //parameterList.appendChild(document.createTextNode("LELELELE"))
    }
    singleOperationDetails.appendChild(addPropertyButton)

    //returnType
    let returnTypeLabel = document.createTextNode('returnType')
    singleOperationDetails.appendChild(returnTypeLabel)
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
    singleOperationDetails.appendChild(returnType)
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
    singleOperationDetails.appendChild(buildTextBox("code", operation, "code"))

    return singleOperation
}

/*
function buildOperations(model) {
    let operationList = document.createElement('div')
    operationList.setAttribute("class", "listContent")

    if(model.operations === []) return operationList

    //accordion and list setup
    model.operations.forEach((operation) => {
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
        parameterList.setAttribute("class", "listContent")
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
                console.log(parameter.type)
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
            parameterList.appendChild(document.createTextNode("LELELELE"))
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
        model.operations.push(new Operation())
    }
    operationList.appendChild(addOperationButton)
    return operationList
}

*/
