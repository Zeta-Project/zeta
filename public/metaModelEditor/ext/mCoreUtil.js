/**
 * Provides functions for testing Elements against their mCore (MoDiGen-Metamodel)-Semantics.
 *
 * @type {{isElement, isGeneralization, isReference, isAbstract, isMEnumContainer}}
 */
var mCoreUtil = (function () {

    var ELEMENT_TYPES = ['uml.Class', 'uml.Abstract'];
    var GENERALIZATION_TYPES = ['uml.Generalization'];
    var REFERENCE_TYPES = ['uml.Aggregation', 'uml.Composition', 'uml.Association'];
    var ABSTRACT_TYPES = ['uml.Abstract', 'uml.Interface'];
    var M_ENUM_CONTAINER_TYPE = 'mcore.Enum';

    var isElement;
    var isGeneralization;
    var isReference;
    var isAbstract;
    var isMEnumContainer;

    /**
     * Checks whether the given object is a MoDiGen-Metamodel-Element.
     *
     * @param element
     * @returns {boolean}
     */
    isElement = function isElement(element) {
        var check = false;

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
        var check = false;

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
        var check = false;

        switch (typeof link) {
            case 'string':
                check = REFERENCE_TYPES.indexOf(link) !== -1;
                break;
            case 'object':
                check = REFERENCE_TYPES.indexOf(link.attributes.type) !== -1;
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
        var check = false;

        switch (typeof element) {
            case 'string':
                check = ABSTRACT_TYPES.indexOf(element) !== -1;
                break;
            case 'object':
                check = ABSTRACT_TYPES.indexOf(element.attributes.type) !== -1;
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
        var check = false;

        switch (typeof element) {
            case 'string':
                check = element === M_ENUM_CONTAINER_TYPE;
                break;
            case 'object':
                check = element.attributes.type === M_ENUM_CONTAINER_TYPE;
        }

        return check;
    };

    return {
        isElement: isElement,
        isGeneralization: isGeneralization,
        isReference: isReference,
        isAbstract: isAbstract,
        isMEnumContainer: isMEnumContainer
    };

})();