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
