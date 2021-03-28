import {IEdge, INode,} from "yfiles";

import {UMLNodeStyle} from "../../vue-cli/src/uml/nodes/styles/UMLNodeStyle";
import {Operation} from "../../vue-cli/src/uml/operations/Method";
import {Parameter} from "../../vue-cli/src/uml/parameters/Parameter";
import {Attribute} from "../../vue-cli/src/uml/attributes/Attribute";
import {UMLEdgeModel} from "../../vue-cli/src/uml/edges/ModelEdgeModel";
import {UMLEdgeStyle} from "../../vue-cli/src/uml/edges/styles/UMLEdgeStyle";

export class PropertyPanel {
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

        //this.divField.className = "propertiesPanel";
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

        if (INode.isInstance(item) && item.style instanceof UMLNodeStyle) {
            let model = item.style.model
            this.div.innerHTML = ""
            this.buildNodeProperties(model, this.div)
        } else if (IEdge.isInstance(item) && item.style instanceof UMLEdgeStyle) {
            let model = item.style.model
            this.div.innerHTML = ""
            this.buildEdgeProperties(model, this.div);

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

    //Todo combine buildEdgeProperties & buildNodeProperties function -> differentiate if edge or node in itemSelectionChanged
    buildEdgeProperties(model, div) {
        //build metaAccordion
        let accordionMeta = document.createElement('button')
        accordionMeta.className = 'collapsible'
        accordionMeta.innerHTML = 'MetaInformation'
        div.appendChild(accordionMeta)

        //add MetaPanel
        div.appendChild(buildEdgeMeta(model))

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
            coll[i].addEventListener("click", function () {
                this.classList.toggle("active");
                let content = this.nextElementSibling;
                if (content.style.maxHeight) {
                    content.style.maxHeight = null;
                } else {
                    content.style.maxHeight = content.scrollHeight + "px";
                    content.parentElement.parentElement.nextElementSibling.style.maxHeight = content.parentElement.parentElement.nextElementSibling.scrollHeight + content.scrollHeight + "px";

                }
            });
        }
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
            coll[i].addEventListener("click", function () {
                this.classList.toggle("active");
                let content = this.nextElementSibling;
                if (content.style.maxHeight) {
                    content.style.maxHeight = null;
                } else {
                    content.style.maxHeight = content.scrollHeight + "px";
                    content.parentElement.parentElement.style.maxHeight = content.parentElement.parentElement.scrollHeight + content.scrollHeight + "px";

                }
            });
        }
    }
}

function buildEdgeMeta(model) {
    let metaContainer = document.createElement('DIV')
    metaContainer.setAttribute("class", "collapsibleContent")

    //name
    metaContainer.appendChild(buildTextBox("name", model, "name"))
    //description
    metaContainer.appendChild(buildTextBox("description", model, "description"))
    //sourceDeletionDeletesTarget
    metaContainer.appendChild(buildCheckBox("sourceDeletionDeletesTarget", model, "sourceDeletionDeletesTarget"))
    //targetDeletionDeletesSource
    metaContainer.appendChild(buildCheckBox("targetDeletionDeletesSource", model, "targetDeletionDeletesSource"))
    //sourceClassName
    metaContainer.appendChild(buildTextBox("sourceClassName", model, "sourceClassName"))
    //targetClassName
    metaContainer.appendChild(buildTextBox("targetClassName", model, "targetClassName"))
    //sourceLowerBounds
    metaContainer.appendChild(buildTextBox("sourceLowerBounds", model, "sourceLowerBounds"))
    //sourceUpperBounds
    metaContainer.appendChild(buildTextBox("sourceUpperBounds", model, "sourceUpperBounds"))
    //targetLowerBounds
    metaContainer.appendChild(buildTextBox("targetLowerBounds", model, "targetLowerBounds"))
    //targetUpperBounds
    metaContainer.appendChild(buildTextBox("targetUpperBounds", model, "targetUpperBounds"))

    return metaContainer
}


function buildMeta(model) {
    let metaContainer = document.createElement('DIV')
    metaContainer.setAttribute("class", "collapsibleContent")

    //name
    metaContainer.appendChild(buildTextBox("Name", model, "className"))

    //description
    metaContainer.appendChild(buildTextBox("Description", model, "description"))

    //isAbstract
    metaContainer.appendChild(buildCheckBox("Abstract", model, "abstract"))

    return metaContainer
}

function buildAttribute(model, attribute) {

    let singleAttribute = document.createElement('div')
    singleAttribute.className = 'singleAttributeContainer'

    /*
    let attributeName = document.createElement('INPUT')
    attributeName.setAttribute('value', attribute.name)
    attributeName.className = 'elementName';
    singleAttribute.appendChild(attributeName);
        */
    let attributeName = document.createElement('INPUT')
    attributeName.setAttribute('value', attribute.name)
    attributeName.className = 'elementName';
    attributeName.oninput = function () {
        attribute.name = attributeName.value
    };
    singleAttribute.appendChild(attributeName);

    let removeAttributeButton = document.createElement('button')
    removeAttributeButton.className = 'removeElementButton'
    singleAttribute.appendChild(removeAttributeButton)
    removeAttributeButton.onclick = function (e) {
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
    attributeListButton.onclick = function () {
        attributeListButton.classList.toggle('listCollapsibleButtonOpen')
        attributeListButton.classList.toggle('listCollapsibleButton')
        let content = this.nextElementSibling;
        if (content.style.maxHeight) {
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
            content.parentElement.parentElement.style.maxHeight = content.parentElement.parentElement.scrollHeight + content.scrollHeight + "px";
        }
    }
    singleAttribute.appendChild(attributeListButton)

    //collapsible Details
    let singleAttributeDetails = document.createElement('div')
    singleAttributeDetails.className = 'collapsibleContent'
    singleAttribute.appendChild(singleAttributeDetails)

    //upper Bound
    //singleAttributeDetails.appendChild(buildTextBox("upperBound", attribute, "upperBound"))

    //lower Bound
    //singleAttributeDetails.appendChild(buildTextBox("lowerBound", attribute, "lowerBound"))

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
    for (let i = 0; i < defaultType.options.length; i++) {
        if (defaultType.options[i].value === attribute.default.type) defaultType.options[i].selected = true
    }
    defaultValue.oninput = function () {
        attribute.default.value = defaultValue.value
    }
    defaultType.onchange = () => {
        for (let i = 0; i < defaultType.options.length; i++) {
            if (defaultType.options[i].selected === true) attribute.default.type = defaultType.options[i].value;
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
    textBox.setAttribute("value", data[value]);
    textBox.oninput = function () {
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
    if (data[boolAttribute]) checkbox.checked = true;
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
    if (model.attributes === []) return attributeContainer
    model.attributes.forEach((attribute) => {
        attributeContainer.appendChild(buildAttribute(model, attribute))
    });

    let addAttributeButton = document.createElement('button')
    addAttributeButton.className = 'addElementButton'
    addAttributeButton.innerHTML = "Add Attribute"
    addAttributeButton.onclick = () => {
        let dummyAttribute = new Attribute()
        model.attributes.push(dummyAttribute)
        attributeContainer.insertBefore(buildAttribute(model, dummyAttribute), addAttributeButton)
    }
    attributeContainer.appendChild(addAttributeButton)

    return attributeContainer
}

function buildParameter(operation, parameter) {
    let singleParameter = document.createElement('div');
    singleParameter.className = 'singleParameterContainer';

    let paraMeterName = document.createElement('INPUT');
    paraMeterName.setAttribute('value', parameter.value || "");
    paraMeterName.className = 'elementName';
    singleParameter.appendChild(paraMeterName);

    let removeParameterButton = document.createElement('button');
    removeParameterButton.className = 'removeElementButton';
    singleParameter.appendChild(removeParameterButton);
    removeParameterButton.onclick = function (e) {
        if (confirm('Parameter entfernen?')) {
            this.parentNode.parentNode.removeChild(this.parentNode);
            let index = operation.parameters.indexOf(parameter);
            operation.parameters.splice(index, 1)
        }
    }
    /*
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
            console.log(content.parentElement.parentElement.parentElement.parentElement);

            content.parentElement.parentElement.parentElement.parentElement.style.maxHeight = content.parentElement.parentElement.parentElement.parentElement.scrollHeight + content.scrollHeight + "px";
            //console.log(content.parentElement.style.height)
            //content.parentElement.style.maxHeight = content.parentElement.style.maxHeight + content.scrollHeight + "px";
        }
    }
    singleParameter.appendChild(parameterListButton)
      */
    //let parameterDetails = document.createElement('div')
    //parameterDetails.className = 'collapsibleContent'
    //singleParameter.appendChild(parameterDetails)

    //returnTypeParameter
    let returnTypeLabel = document.createTextNode('returnTypeParameter')
    singleParameter.appendChild(returnTypeLabel)
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
    singleParameter.appendChild(returnType)
    //set returnType
    for (let i = 0; i < returnType.options.length; i++) {
        //console.log(parameter.type)
        if (returnType.options[i].value === parameter.type) returnType.options[i].selected = true
    }
    //set operation.returnType
    returnType.onchange = () => {
        for (let i = 0; i < returnType.options.length; i++) {
            if (returnType.options[i].selected === true) {
                parameter.type = returnType.options[i].value;
            }
        }
    }

    return singleParameter;
}

function buildOperations(model) {
    let operationContainer = document.createElement('div')
    operationContainer.setAttribute("class", "collapsibleContent")

    if (model.operations === []) return operationContainer
    model.operations.forEach((operation) => {
        operationContainer.appendChild(buildOperation(model, operation))
    });

    let addOperationButton = document.createElement('button')
    addOperationButton.className = 'addElementButton'
    addOperationButton.innerHTML = "Add Method"
    addOperationButton.onclick = () => {
        let dummyOperation = new Operation()
        model.operations.push(dummyOperation)
        operationContainer.insertBefore(buildOperation(model, dummyOperation), addOperationButton)
    }
    operationContainer.appendChild(addOperationButton)

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
    removeOperationButton.onclick = function (e) {
        if (confirm('Method entfernen?')) {
            this.parentNode.parentNode.removeChild(this.parentNode)
            let index = model.operations.indexOf(operation)
            model.operations.splice(index, 1)
        } else {
            // Do nothing!
        }
    }

    let operationListButton = document.createElement('button')
    operationListButton.className = 'listCollapsibleButton'
    operationListButton.onclick = function () {
        operationListButton.classList.toggle('listCollapsibleButtonOpen')
        operationListButton.classList.toggle('listCollapsibleButton')
        let content = this.nextElementSibling;
        if (content.style.maxHeight) {
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
            content.parentElement.parentElement.style.maxHeight = content.parentElement.parentElement.scrollHeight + content.scrollHeight + "px";
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
    addPropertyButton.className = 'addElementButton'
    addPropertyButton.innerHTML = "Add Parameter"
    addPropertyButton.onclick = () => {
        let dummyParameter = new Parameter()
        operation.parameters.push(dummyParameter)
        singleOperationDetails.insertBefore(buildParameter(operation, dummyParameter), addPropertyButton)

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
    for (let i = 0; i < returnType.options.length; i++) {
        if (returnType.options[i].value === operation.returnType) returnType.options[i].selected = true
    }
    //set operation.returnType
    returnType.onchange = () => {
        for (let i = 0; i < returnType.options.length; i++) {
            if (returnType.options[i].selected === true) {
                operation.returnType = returnType.options[i].value;
            }
        }
    }

    //code
    singleOperationDetails.appendChild(buildTextBox("code", operation, "code"))

    return singleOperation
}