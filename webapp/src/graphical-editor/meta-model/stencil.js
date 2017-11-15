import $ from 'jquery';
import joint from 'jointjs';

var Stencil = {};

Stencil.groups = {
    classes : {index : 5, label : 'Classes'}
};

Stencil.shapes = {
    classes : [
        new joint.shapes.uml.Class({
            name : 'Class',
            attrs : {
                '.' : {filter : Stencil.filter},
                '.uml-class-name-text' : {'font-size' : 9},
                '.uml-class-attrs-text' : {'font-size' : 9},
                '.uml-class-methods-text' : {'font-size' : 9}
            }
        }),
        new joint.shapes.uml.Abstract({
            name : 'Abstract',
            attrs : {
                '.' : {filter : Stencil.filter},
                '.uml-class-name-text' : {'font-size' : 9},
                '.uml-class-attrs-text' : {'font-size' : 9},
                '.uml-class-methods-text' : {'font-size' : 9}
            }
        })
    ]
};

$(".stencil-toggle-icon-wrapper").on("click", function() {
    $(".stencil-toggle-icon-wrapper").toggleClass("glyphicon-menu-right");
    $(".paper-container").toggleClass("paper-container-stencil-hidden");
    $(".stencil-container").toggleClass("stencil-container-hidden");
    $(".stencil-toggle-container").toggleClass("toggle-container-hidden");
});

export default Stencil;