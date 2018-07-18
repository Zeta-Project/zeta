import $ from 'jquery';
import GeneratorFactory from "../generator/GeneratorFactory";

//TODO import {linkhelper} from '../generator/editor/LinkHelperGenerator'

export default (function modelExporter() {
    'use strict';

    let exportModel;
    let _buildNodes;
    let _buildEdges;
    let _getAttributeValue;
    let _graph;
    let _showExportSuccess;
    let _showExportFailure;
    let _createAttributeInfo;

    exportModel = function (graph) {
        $("[data-hide]").on("click", function () {
            $("." + $(this).attr("data-hide")).hide();
        });

        _graph = graph;
        const nodes = _buildNodes();
        const edges = _buildEdges();
        let uiState = JSON.stringify(_graph.toJSON());

        let data = JSON.stringify({
            name: window._global_model_name,
            graphicalDslId: window._global_graph_type,
            nodes: nodes,
            edges: edges,
            attributes: [],
            attributeValues: {},
            methods: [],
            uiState: uiState
        });

        $.ajax({
            type: 'PUT',
            url: '/rest/v1/models/' + window._global_uuid + "/definition",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data: data,
            success: function (data, textStatus, jqXHR) {
                _showExportSuccess();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                _showExportFailure("Failure, there occurred an error during saving!");
            }
        });
    };


    _buildNodes = function () {
        let elements = [];
        _graph.getElements().forEach(function (ele) {

            // console.log(ele);
            let element = {
                name: ele.id,
                className: ele.attributes.className,
                outputEdgeNames: [],
                inputEdgeNames: [],
                attributes: [],
                attributeValues: {},
                methods: []
            };

            let attrs = ele.attributes.attrs;
            for (let key in attrs) {
                if (_.include(key, "text")) {
                    let attrName = globalMClassAttributeInfo.find(element => element.id === attrs[key].id);

                    if (attrName !== undefined) {
                        element.attributes.push(_createAttributeInfo(attrName));

                        element.attributeValues[attrName.name] = [];

                        if (!Array.isArray(attrs[key].text)) {
                            attrs[key].text = [attrs[key].text];
                        }

                        attrs[key].text.forEach(attr => {
                            element.attributeValues[attrName.name].push({value: attr || '', type: attrName.type});
                        });
                    }
                }
            }

            // add all attributes that have no value
            globalMClassAttributeInfo.forEach(function (info) {
                if (!element.attributes.hasOwnProperty(info.name)) {
                    element.attributes[info.name] = [];
                }
            });

            _graph.getConnectedLinks(ele, {outbound: true}).forEach(function (link) {
                element.outputEdgeNames.push(link.attributes.id);
            });

            _graph.getConnectedLinks(ele, {inbound: true}).forEach(function (link) {
                element.inputEdgeNames.push(link.attributes.id);
            });
            elements.push(element);
        });

        return elements;
    };


    _buildEdges = function () {
        let elements = [];
        _graph.getLinks().forEach(function (link) {

            // console.log(link);
            let element = {
                name: link.id,
                referenceName: link.attributes.mReference,
                sourceNodeName: {},
                targetNodeName: {},
                attributes: [],
                attributeValues: {},
                methods: []
            };

            element.sourceNodeName = link.attributes.source.id;
            element.targetNodeName = link.attributes.target.id;

            const mReferenceAttributesInfos = globalMReferenceAttributeInfo[link.attributes.mReference];
            if (mReferenceAttributesInfos) {
                element.attributes = mReferenceAttributesInfos.map(attr => {
                    return _createAttributeInfo(attr)
                });

                if ($.isEmptyObject(attributePositionMarker)) {
                    GeneratorFactory.inspector.getEdgeInspectorDefs();
                }

                const attributeValues = link.attributes[link.attributes.mReference];

                if (attributeValues) {
                    for (let i = 0; i < mReferenceAttributesInfos.length; i++) {
                      const info = mReferenceAttributesInfos[i];
                        if (!Array.isArray(attributeValues[i])) {
                            attributeValues[i] = [attributeValues[i]];
                        }
                        if (attributeValues[i] && attributeValues[i].length > 0) {
                            element.attributeValues[info.name] = [];
                            attributeValues[i].forEach(attrValue => {
                                element.attributeValues[info.name].push(
                                    {
                                        'value': attrValue,
                                        'type': info.type
                                    }
                                )
                            });
                        }
                    }
                }
            }
            elements.push(element);
        });
        return elements;
    };

    _createAttributeInfo = (attribute) => {
        return {
            'name': attribute.name,
            'globalUnique': attribute.globalUnique,
            'localUnique': attribute.localUnique,
            'type': attribute.type,
            'default': {'value': attribute.default.value, 'type': attribute.default.type},
            'constant': attribute.constant,
            'singleAssignment': attribute.singleAssignment,
            'expression': attribute.expression,
            'ordered': attribute.ordered,
            'transient': attribute.transient
        }
    };

    _getAttributeValue = function (value, type) {
        let ret = value;
        switch (type) {
            case 'Bool':
                ret = value.toLowerCase() === 'true';
                break;
            case 'Int':
                $.isNumeric(value) ? ret = parseInt(value) : ret = 0;
                break;
            case 'Double':
                $.isNumeric(value) ? ret = parseFloat(ret) : ret = 0.0;
                break;
        }
        return ret;
    };

    _showExportSuccess = function () {
        $("#success-panel").fadeOut('slow', function () {
            $("#error-panel").fadeOut('slow', function () {
                $("#success-panel").show();
                $("#success-panel").find("div").text("Success, model saved!");
                $("#success-panel").fadeIn('slow');
            });
        });
    };

    _showExportFailure = function (reason) {
        $("#success-panel").fadeOut('slow', function () {
            $("#error-panel").fadeOut('slow', function () {
                $("#error-panel").show();
                $("#error-panel").find("div").text(reason);
                $("#error-panel").fadeIn('slow');
            });
        });
    };

    return {
        exportModel: exportModel
    };

})();
