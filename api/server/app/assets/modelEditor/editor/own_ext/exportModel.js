var modelExporter = (function modelExporter () {
    'use strict';

    var exportModel;
    var _buildElements;
    var _getAttributeValue;
    var _graph;
    var _showExportSuccess;
    var _showExportFailure;

    exportModel = function(graph) {
        $("[data-hide]").on("click", function(){
            $("." + $(this).attr("data-hide")).hide();
        });

        _graph = graph;
        var elements = _buildElements();
        var uiState = JSON.stringify(_graph.toJSON());

        var data = JSON.stringify({
            name: window._global_model_name,
            metaModelId: window._global_graph_type,
            elements: elements,
            uiState: uiState
        });

        $.ajax({
            type: 'PUT',
            url: '/models/' + window._global_uuid + "/definition",
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


    _buildElements = function() {
        var elements = [];

        _graph.getElements().forEach(function(ele) {
            var element = {
                id: ele.id,
                mClass: ele.attributes.mClass,
                outputs: {},
                inputs: {},
                attributes: {}
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
                if(element.outputs.hasOwnProperty(link.attributes.mReference)) {
                    element.outputs[link.attributes.mReference].push(link.attributes.id);
                } else {
                    element.outputs[link.attributes.mReference] = [link.attributes.id];
                }
            });

            _graph.getConnectedLinks(ele, {inbound: true}).forEach(function(link) {
                if(element.inputs.hasOwnProperty(link.attributes.mReference)) {
                    element.inputs[link.attributes.mReference].push(link.attributes.id);
                } else {
                    element.inputs[link.attributes.mReference] = [link.attributes.id];
                }
            });
            elements.push(element);
        });

        _graph.getLinks().forEach(function(link) {
            var element = {
                id: link.id,
                mReference: link.attributes.mReference,
                source: {},
                target: {},
                attributes: {}
            };
            // In the Metamodel a connection can have multiple sources/targets
            // but in joint.js this is not possible
            element.source[link.attributes.sourceAttribute] = [link.attributes.source.id];
            element.target[link.attributes.targetAttribute] = [link.attributes.target.id]

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
