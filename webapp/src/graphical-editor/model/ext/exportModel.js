import $ from 'jquery';
import {linkhelper} from '../generator/editor/LinkHelperGenerator'
//TODO import {linkhelper} from '../generator/editor/LinkHelperGenerator'

export default (function modelExporter () {
    'use strict';

    var exportModel;
    var _buildNodes;
    var _buildEdges;
    var _getAttributeValue;
    var _graph;
    var _showExportSuccess;
    var _showExportFailure;

    exportModel = function(graph) {
        $("[data-hide]").on("click", function(){
            $("." + $(this).attr("data-hide")).hide();
        });

        _graph = graph;
        const nodes = _buildNodes();
        const edges = _buildEdges();
        var uiState = JSON.stringify(_graph.toJSON());

        var data = JSON.stringify({
            name: window._global_model_name,
            graphicalDslId: window._global_graph_type,
            nodes: nodes,
            edges: edges,
            attributes: [], // TODO
            attributeValues: {},
            methods: [],
            uiState: uiState
        });

        console.log("SaveModel - Data: ");
        console.log(data);

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


    _buildNodes = function() {
        var elements = [];
        _graph.getElements().forEach(function(ele) {
            var element = {
                name: ele.id,
                className: ele.attributes.mClass,
                outputEdgeNames: [],
                inputEdgeNames: [],
                attributes: [],
                attributeValues: {},
                methods: []
            };

            var attrs = ele.attributes.attrs;
            for(var key in attrs) {
                if(_.include(key, "text")) {
                    var attrName = ele.attributes.mClassAttributeInfo.find(element => element.id === attrs[key].id);
                    if(attrName !== undefined) {
                        element.attributes[attrName.name] = attrs[key].text;
                    }
                }
            }

            // add all attributes that have no value
            ele.attributes.mClassAttributeInfo.forEach(function(info) {
                if(!element.attributes.hasOwnProperty(info.name)) {
                    element.attributes[info.name] = [];
                }
            });

            _graph.getConnectedLinks(ele, {outbound: true}).forEach(function(link) {
                element.outputEdgeNames.push(link.attributes.source.id);
            });

            _graph.getConnectedLinks(ele, {inbound: true}).forEach(function(link) {
                   element.inputEdgeNames.push(link.attributes.target.id);
            });
            elements.push(element);
        });

        return elements;
    };


    _buildEdges = function() {
        var elements = [];
        _graph.getLinks().forEach(function(link) {
            var element = {
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

            // add attributes
            link.attributes.labels.forEach(function(label) {
                var attributeName = linkhelper.mapping[link.attributes.mReference][label.id];
                element.attributes[attributeName] = [label.attrs.text.text];
            });

            elements.push(element);
        });
        return elements;
    };

    _getAttributeValue = function(value, type) {
        var ret = value;
        switch(type) {
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

    _showExportSuccess = function() {
        $("#success-panel").fadeOut('slow', function() {
            $("#error-panel").fadeOut('slow', function() {
                $("#success-panel").show();
                $("#success-panel").find("div").text("Success, model saved!");
                $("#success-panel").fadeIn('slow');
            });
        });
    };

    _showExportFailure = function(reason) {
        $("#success-panel").fadeOut('slow', function() {
            $("#error-panel").fadeOut('slow', function() {
                $("#error-panel").show();
                $("#error-panel").find("div").text(reason);
                $("#error-panel").fadeIn('slow');
            });
        });
    };

    return {
        exportModel : exportModel
    };

})();
