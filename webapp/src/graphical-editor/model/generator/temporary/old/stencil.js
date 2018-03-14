//OLD
import $ from 'jquery';
import joint from 'jointjs';
import Stencil from '../../../stencil';
import './shape';
import {getShapeStyle} from "./elementAndInlineStyle";
import {getDiagramHighlighting} from "./style";

export default Stencil

Stencil.groups = {interface: {index: 3, label: 'Interface'}, abstractClass: {index: 2, label: 'AbstractClass'}, class: {index: 1, label: 'Class'}};

var classNode = new joint.shapes.zeta.klasse({

    nodeName: 'classNode',
    mClass: 'Klasse',
    mClassAttributeInfo: []
});

var abClassNode = new joint.shapes.zeta.abstractKlasse({

    nodeName: 'abClassNode',
    mClass: 'AbstractKlasse',
    mClassAttributeInfo: []
});

var inClassNode = new joint.shapes.zeta.interface({

    nodeName: 'inClassNode',
    mClass: 'InterfaceKlasse',
    mClassAttributeInfo: []
});


Stencil.shapes = {

    interface: [
        inClassNode

    ]
    ,
    class: [
        classNode

    ]
    ,
    abstractClass: [
        abClassNode

    ]

};


$(document).ready(function () {

    classNode.attr(getShapeStyle("klasse"));


    abClassNode.attr(getShapeStyle("abstractKlasse"));


    inClassNode.attr(getShapeStyle("interface"));


    var style = document.createElement('style');
    style.id = 'highlighting-style';
    style.type = 'text/css';
    style.innerHTML = getDiagramHighlighting("X");
    document.getElementsByTagName('head')[0].appendChild(style);

});
