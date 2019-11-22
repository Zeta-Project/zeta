/**
 * Provides functions for testing Elements against their mCore (MoDiGen-Metamodel)-Semantics.
 *
 * @type {{isElement, isGeneralization, isReference, isAbstract, isMEnumContainer}}
 */
//Todo check each element for datatype in yfiles env
export default (function () {

    const ELEMENT_TYPES = ['uml.Class', 'uml.Abstract'];
    const GENERALIZATION_TYPES = ['uml.Generalization'];
    const REFERENCE_TYPES = ['uml.Aggregation', 'uml.Composition', 'uml.Association'];
    const ABSTRACT_TYPES = ['uml.Abstract', 'uml.Interface'];
    const M_ENUM_CONTAINER_TYPE = 'mcore.Enum';
    const M_ATTRIBUTE_CONTAINER_TYPE = 'mcore.Attribute';

    let isElement;
    let isGeneralization;
    let isReference;
    let isAbstract;
    let isMEnumContainer;
    let isMAttributeContainer;

    /**
     * Checks whether the given object is a MoDiGen-Metamodel-Element.
     *
     * @param element
     * @returns {boolean}
     */
    isElement = function isElement(element) {
        let check = false;

        switch (typeof element) {
            case 'string':
                check = ELEMENT_TYPES.indexOf(element) !== -1;
                break;
            case 'object':
                check = ELEMENT_TYPES.indexOf(element.attributes.type) !== -1;
        }
        return check;
    };

    /**
     * Checks whether the given link-type is a Generalization
     * (these types have to be handled differently than "normal" references when exporting).
     *
     * @param link
     * @returns {boolean}
     */
    isGeneralization = function isGeneralization(link) {
        let check = false;

        switch (typeof link) {
            case 'string':
                check = GENERALIZATION_TYPES.indexOf(link) !== -1;
                break;
            case 'object':
                check = GENERALIZATION_TYPES.indexOf(link.attributes.type) !== -1;
        }

        return check;
    };

    /**
     * Checks whether the given link is a reference.
     * (E. g. Generalizations are no references in MoDiGen-Metamodel-Semantics).
     *
     * @param link
     * @returns {boolean}
     */
    isReference = function isReference(link) {
        let check = false;

        switch (typeof link) {
            case 'string':
                check = REFERENCE_TYPES.indexOf(link) !== -1;
                break;
            case 'object':
                check = true
                //check = REFERENCE_TYPES.indexOf(link.attribute.type) !== -1;
        }

        return check;
    };

    /**
     * Checks whether the element is an abstract class.
     *
     * @param element
     * @returns {boolean}
     */
    isAbstract = function isAbstract(element) {
        let check = false;

        switch (typeof element) {
            case 'string':
                check = ABSTRACT_TYPES.indexOf(element) !== -1;
                break;
            case 'object':
                check = true
                //check = ABSTRACT_TYPES.indexOf(element.attributes.type) !== -1;
        }

        return check;
    };

    /**
     * Checks whether the element is the mEnumContainer.
     * mEnumContainer is implemented as a normal class on the paper, so it has to be distinguished.
     * 
     * @param element
     * @returns {boolean}
     */
    isMEnumContainer = function isMEnumContainer(element) {
        let check = false;

        switch (typeof element) {
            case 'string':
                check = element === M_ENUM_CONTAINER_TYPE;
                break;
            case 'object':
                return true
                //check = element.attributes.type === M_ENUM_CONTAINER_TYPE;
        }

        return check;
    };

    /**
     * Checks whether the element is the isMAttributeContainer.
     * isMAttributeContainer is implemented as a normal class on the paper, so it has to be distinguished.
     *
     * @param element
     * @returns {boolean}
     */
    isMAttributeContainer = function isMAttributeContainer(element) {
        let check = false;

        switch (typeof element) {
            case 'string':
                check = element === M_ATTRIBUTE_CONTAINER_TYPE;
                break;
            case 'object':
                check = trueS
                //check = element.attributes.type === M_ATTRIBUTE_CONTAINER_TYPE;
        }

        return check;
    };

    return {
        isElement: isElement,
        isGeneralization: isGeneralization,
        isReference: isReference,
        isAbstract: isAbstract,
        isMEnumContainer: isMEnumContainer,
        isMAttributeContainer: isMAttributeContainer
    };

})();