import joint from 'jointjs';

import StyleGenerator from './style/StyleGenerator'
import InspectorGenerator from './editor/InspectorGenerator'
import LinkHelperGenerator from './editor/LinkHelperGenerator'
import StencilGenerator from './editor/StencilGenerator'
import ValidatorGenerator from './editor/ValidatorGenerator'

import ShapeDefinitionGenerator from './shape/ShapeDefinitionGenerator'
import ShapeStyleGenerator from './shape/ShapeStyleGenerator'
import ConnectionDefinitionGenerator from './shape/connectionDefinitionGenerator/ConnectionDefinitionGenerator'

let generators = null;

function checkInitialized() {
    if (generators === null) {
        console.error("The GeneratorFactory needs to be initialized before getting any generator");
    }
}

function createGenerators(styleData, diagramData, shapeData, conceptData) {

    const style = new StyleGenerator(styleData);
    const shapeStyle = new ShapeStyleGenerator(shapeData, style); // TODO ShapeStyleGenerator is only partially updated to V2
    const shapeDefinition = new ShapeDefinitionGenerator(shapeData, shapeStyle);
    const connectionDefinition = new ConnectionDefinitionGenerator(shapeData, style);

    Object.assign(joint.shapes.zeta, shapeDefinition.zeta);

    const stencil = new StencilGenerator(diagramData, shapeData, conceptData, shapeStyle, style); // TODO update StencilGenerator to V2
    const inspector = new InspectorGenerator(shapeData, shapeDefinition); // TODO update InspectorGenerator to V2
    const linkHelper = new LinkHelperGenerator(diagramData); // TODO update LinkHelperGenerator to V2
    const validator = new ValidatorGenerator(shapeData, conceptData); // TODO update ValidatorGenerator to V2

    generators = {
        style,
        shapeDefinition,
        shapeStyle,
        connectionDefinition,

        inspector,
        linkHelper,
        stencil,
        validator
    };

}

export default class GeneratorFactory {

    static initialize() {
        return new Promise((resolve, reject) => {

            const metaModelId = window._global_graph_type;
            const credentials = {credentials: 'same-origin'};

            Promise.all([
                fetch(`/rest/v2/meta-models/${metaModelId}/style`, credentials).then(r => r.json()),
                fetch(`/rest/v2/meta-models/${metaModelId}/diagram`, credentials).then(r => r.json()),
                fetch(`/rest/v2/meta-models/${metaModelId}/shape`, credentials).then(r => r.json()),
                fetch(`/rest/v1/meta-models/${metaModelId}`, credentials).then(r => r.json()),
            ]).then(([style, diagram, shape, concept]) => {
                createGenerators(style['styles'], diagram, mockJson, concept['concept']); // TODO remove mocked version
                resolve();
            }).catch(error => {
                console.error(`Error fetching Rest-API: ${error}`);
                reject(error);
            });

        });
    }

    static get style() {
        checkInitialized();
        return generators.style;
    }

    static get shapeDefinition() {
        checkInitialized();
        return generators.shapeDefinition;
    }

    static get shapeStyle() {
        checkInitialized();
        return generators.shapeStyle;
    }

    static get connectionDefinition() {
        checkInitialized();
        return generators.connectionDefinition;
    }

    static get inspector() {
        checkInitialized();
        return generators.inspector;
    }

    static get linkHelper() {
        checkInitialized();
        return generators.linkHelper;
    }

    static get stencil() {
        checkInitialized();
        return generators.stencil;
    }

    static get validator() {
        checkInitialized();
        return generators.validator;
    }

}

const mockJson = {
    "nodes" : [ {
        "name" : "TeamAnchorNode",
        "conceptElement" : "TeamAnchor",
        "edges" : [ ],
        "size" : {
            "width" : 0,
            "height" : 0,
            "widthMax" : 50,
            "widthMin" : 50,
            "heightMax" : 50,
            "heightMin" : 50
        },
        "style" : {
            "name" : "default",
            "description" : "default",
            "background" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                }
            },
            "font" : {
                "name" : "Arial",
                "bold" : false,
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "italic" : false,
                "size" : 10
            },
            "line" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "style" : "solid",
                "width" : 1
            },
            "transparency" : 1
        },
        "resizing" : {
            "horizontal" : false,
            "vertical" : false,
            "proportional" : false
        },
        "geoElements" : [ {
            "type" : "ellipse",
            "size" : {
                "width" : 50,
                "height" : 50
            },
            "position" : {
                "x" : 0,
                "y" : 0
            },
            "childGeoElements" : [ {
                "type" : "statictext",
                "size" : {
                    "width" : 50,
                    "height" : 50
                },
                "position" : {
                    "x" : 6,
                    "y" : 17
                },
                "text" : "Team",
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "TeamAnchorStyle",
                    "description" : "Style for a team anchor",
                    "background" : {
                        "color" : {
                            "r" : 238,
                            "g" : 34,
                            "b" : 34,
                            "a" : 1,
                            "rgb" : "rgb(238,34,34)",
                            "rgba" : "rgba(238,34,34,1.0)",
                            "hex" : "#ee2222"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : true,
                        "color" : {
                            "r" : 255,
                            "g" : 255,
                            "b" : 255,
                            "a" : 1,
                            "rgb" : "rgb(255,255,255)",
                            "rgba" : "rgba(255,255,255,1.0)",
                            "hex" : "#ffffff"
                        },
                        "italic" : true,
                        "size" : 14
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "dash",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000a8bf12e2"
            } ],
            "style" : {
                "name" : "TeamAnchorStyle",
                "description" : "Style for a team anchor",
                "background" : {
                    "color" : {
                        "r" : 238,
                        "g" : 34,
                        "b" : 34,
                        "a" : 1,
                        "rgb" : "rgb(238,34,34)",
                        "rgba" : "rgba(238,34,34,1.0)",
                        "hex" : "#ee2222"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : true,
                    "color" : {
                        "r" : 255,
                        "g" : 255,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(255,255,255)",
                        "rgba" : "rgba(255,255,255,1.0)",
                        "hex" : "#ffffff"
                    },
                    "italic" : true,
                    "size" : 14
                },
                "line" : {
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "style" : "dash",
                    "width" : 1
                },
                "transparency" : 1
            },
            "id" : "00000000-0000-0000-0000-00006dce8774"
        } ]
    }, {
        "name" : "PeriodAnchorNode",
        "conceptElement" : "PeriodAnchor",
        "edges" : [ ],
        "size" : {
            "width" : 0,
            "height" : 0,
            "widthMax" : 50,
            "widthMin" : 50,
            "heightMax" : 50,
            "heightMin" : 50
        },
        "style" : {
            "name" : "default",
            "description" : "default",
            "background" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                }
            },
            "font" : {
                "name" : "Arial",
                "bold" : false,
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "italic" : false,
                "size" : 10
            },
            "line" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "style" : "solid",
                "width" : 1
            },
            "transparency" : 1
        },
        "resizing" : {
            "horizontal" : false,
            "vertical" : false,
            "proportional" : false
        },
        "geoElements" : [ {
            "type" : "ellipse",
            "size" : {
                "width" : 50,
                "height" : 50
            },
            "position" : {
                "x" : 0,
                "y" : 0
            },
            "childGeoElements" : [ {
                "type" : "statictext",
                "size" : {
                    "width" : 50,
                    "height" : 50
                },
                "position" : {
                    "x" : 1,
                    "y" : 17
                },
                "text" : "Period",
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "PeriodAnchorStyle",
                    "description" : "Style for a period anchor",
                    "background" : {
                        "color" : {
                            "r" : 85,
                            "g" : 85,
                            "b" : 255,
                            "a" : 1,
                            "rgb" : "rgb(85,85,255)",
                            "rgba" : "rgba(85,85,255,1.0)",
                            "hex" : "#5555ff"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : true,
                        "color" : {
                            "r" : 255,
                            "g" : 255,
                            "b" : 255,
                            "a" : 1,
                            "rgb" : "rgb(255,255,255)",
                            "rgba" : "rgba(255,255,255,1.0)",
                            "hex" : "#ffffff"
                        },
                        "italic" : true,
                        "size" : 14
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "dash",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000c8a904f7"
            } ],
            "style" : {
                "name" : "PeriodAnchorStyle",
                "description" : "Style for a period anchor",
                "background" : {
                    "color" : {
                        "r" : 85,
                        "g" : 85,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(85,85,255)",
                        "rgba" : "rgba(85,85,255,1.0)",
                        "hex" : "#5555ff"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : true,
                    "color" : {
                        "r" : 255,
                        "g" : 255,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(255,255,255)",
                        "rgba" : "rgba(255,255,255,1.0)",
                        "hex" : "#ffffff"
                    },
                    "italic" : true,
                    "size" : 14
                },
                "line" : {
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "style" : "dash",
                    "width" : 1
                },
                "transparency" : 1
            },
            "id" : "00000000-0000-0000-0000-0000285d7121"
        } ]
    }, {
        "name" : "EntityNode",
        "conceptElement" : "Entity",
        "edges" : [ ],
        "size" : {
            "width" : 0,
            "height" : 0,
            "widthMax" : 200,
            "widthMin" : 200,
            "heightMax" : 153,
            "heightMin" : 153
        },
        "style" : {
            "name" : "default",
            "description" : "default",
            "background" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                }
            },
            "font" : {
                "name" : "Arial",
                "bold" : false,
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "italic" : false,
                "size" : 10
            },
            "line" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "style" : "solid",
                "width" : 1
            },
            "transparency" : 1
        },
        "resizing" : {
            "horizontal" : false,
            "vertical" : false,
            "proportional" : false
        },
        "geoElements" : [ {
            "type" : "rectangle",
            "size" : {
                "width" : 200,
                "height" : 30
            },
            "position" : {
                "x" : 0,
                "y" : 0
            },
            "childGeoElements" : [ {
                "type" : "textfield",
                "identifier" : "name",
                "textBody" : "Entity name",
                "size" : {
                    "width" : 190,
                    "height" : 16
                },
                "position" : {
                    "x" : 5,
                    "y" : 7
                },
                "editable" : true,
                "multiline" : false,
                "align" : {
                    "horizontal" : "middle",
                    "vertical" : "middle"
                },
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "HeadingBoxStyle",
                    "description" : "Heading box for entities",
                    "background" : {
                        "color" : {
                            "r" : 169,
                            "g" : 169,
                            "b" : 169,
                            "a" : 1,
                            "rgb" : "rgb(169,169,169)",
                            "rgba" : "rgba(169,169,169,1.0)",
                            "hex" : "#a9a9a9"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : false,
                        "color" : {
                            "r" : 255,
                            "g" : 255,
                            "b" : 255,
                            "a" : 1,
                            "rgb" : "rgb(255,255,255)",
                            "rgba" : "rgba(255,255,255,1.0)",
                            "hex" : "#ffffff"
                        },
                        "italic" : false,
                        "size" : 16
                    },
                    "line" : {
                        "color" : {
                            "r" : 169,
                            "g" : 169,
                            "b" : 169,
                            "a" : 1,
                            "rgb" : "rgb(169,169,169)",
                            "rgba" : "rgba(169,169,169,1.0)",
                            "hex" : "#a9a9a9"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000841e3210"
            } ],
            "style" : {
                "name" : "HeadingBoxStyle",
                "description" : "Heading box for entities",
                "background" : {
                    "color" : {
                        "r" : 169,
                        "g" : 169,
                        "b" : 169,
                        "a" : 1,
                        "rgb" : "rgb(169,169,169)",
                        "rgba" : "rgba(169,169,169,1.0)",
                        "hex" : "#a9a9a9"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : false,
                    "color" : {
                        "r" : 255,
                        "g" : 255,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(255,255,255)",
                        "rgba" : "rgba(255,255,255,1.0)",
                        "hex" : "#ffffff"
                    },
                    "italic" : false,
                    "size" : 16
                },
                "line" : {
                    "color" : {
                        "r" : 169,
                        "g" : 169,
                        "b" : 169,
                        "a" : 1,
                        "rgb" : "rgb(169,169,169)",
                        "rgba" : "rgba(169,169,169,1.0)",
                        "hex" : "#a9a9a9"
                    },
                    "style" : "solid",
                    "width" : 1
                },
                "transparency" : 1
            },
            "id" : "00000000-0000-0000-0000-00006913d2df"
        }, {
            "type" : "rectangle",
            "size" : {
                "width" : 200,
                "height" : 41
            },
            "position" : {
                "x" : 0,
                "y" : 30
            },
            "childGeoElements" : [ {
                "type" : "statictext",
                "size" : {
                    "width" : 190,
                    "height" : 14
                },
                "position" : {
                    "x" : 5,
                    "y" : 5
                },
                "text" : "FIX: ",
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "LabelStyle",
                    "description" : "Small label",
                    "background" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : true,
                        "color" : {
                            "r" : 169,
                            "g" : 169,
                            "b" : 169,
                            "a" : 1,
                            "rgb" : "rgb(169,169,169)",
                            "rgba" : "rgba(169,169,169,1.0)",
                            "hex" : "#a9a9a9"
                        },
                        "italic" : false,
                        "size" : 13
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000f94b676f"
            }, {
                "type" : "textfield",
                "identifier" : "fix",
                "textBody" : "...",
                "size" : {
                    "width" : 190,
                    "height" : 14
                },
                "position" : {
                    "x" : 5,
                    "y" : 22
                },
                "editable" : true,
                "multiline" : true,
                "align" : {
                    "horizontal" : "middle",
                    "vertical" : "middle"
                },
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "TextStyle",
                    "description" : "General text style",
                    "background" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : false,
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "italic" : false,
                        "size" : 14
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-000030ff3a62"
            } ],
            "style" : {
                "name" : "BoxStyle",
                "description" : "A simple black/white box",
                "background" : {
                    "color" : {
                        "r" : 255,
                        "g" : 255,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(255,255,255)",
                        "rgba" : "rgba(255,255,255,1.0)",
                        "hex" : "#ffffff"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : false,
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "italic" : false,
                    "size" : 10
                },
                "line" : {
                    "color" : {
                        "r" : 169,
                        "g" : 169,
                        "b" : 169,
                        "a" : 1,
                        "rgb" : "rgb(169,169,169)",
                        "rgba" : "rgba(169,169,169,1.0)",
                        "hex" : "#a9a9a9"
                    },
                    "style" : "solid",
                    "width" : 1
                },
                "transparency" : 1
            },
            "id" : "00000000-0000-0000-0000-00004a00c988"
        }, {
            "type" : "rectangle",
            "size" : {
                "width" : 200,
                "height" : 41
            },
            "position" : {
                "x" : 0,
                "y" : 71
            },
            "childGeoElements" : [ {
                "type" : "statictext",
                "size" : {
                    "width" : 190,
                    "height" : 14
                },
                "position" : {
                    "x" : 5,
                    "y" : 5
                },
                "text" : "IN: ",
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "LabelStyle",
                    "description" : "Small label",
                    "background" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : true,
                        "color" : {
                            "r" : 169,
                            "g" : 169,
                            "b" : 169,
                            "a" : 1,
                            "rgb" : "rgb(169,169,169)",
                            "rgba" : "rgba(169,169,169,1.0)",
                            "hex" : "#a9a9a9"
                        },
                        "italic" : false,
                        "size" : 13
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000523eb1cc"
            }, {
                "type" : "textfield",
                "identifier" : "in",
                "textBody" : "...",
                "size" : {
                    "width" : 190,
                    "height" : 14
                },
                "position" : {
                    "x" : 5,
                    "y" : 22
                },
                "editable" : true,
                "multiline" : true,
                "align" : {
                    "horizontal" : "middle",
                    "vertical" : "middle"
                },
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "TextStyle",
                    "description" : "General text style",
                    "background" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : false,
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "italic" : false,
                        "size" : 14
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-00002bc5c0ec"
            } ],
            "style" : {
                "name" : "BoxStyle",
                "description" : "A simple black/white box",
                "background" : {
                    "color" : {
                        "r" : 255,
                        "g" : 255,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(255,255,255)",
                        "rgba" : "rgba(255,255,255,1.0)",
                        "hex" : "#ffffff"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : false,
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "italic" : false,
                    "size" : 10
                },
                "line" : {
                    "color" : {
                        "r" : 169,
                        "g" : 169,
                        "b" : 169,
                        "a" : 1,
                        "rgb" : "rgb(169,169,169)",
                        "rgba" : "rgba(169,169,169,1.0)",
                        "hex" : "#a9a9a9"
                    },
                    "style" : "solid",
                    "width" : 1
                },
                "transparency" : 1
            },
            "id" : "00000000-0000-0000-0000-0000ae1fb249"
        }, {
            "type" : "rectangle",
            "size" : {
                "width" : 200,
                "height" : 41
            },
            "position" : {
                "x" : 0,
                "y" : 112
            },
            "childGeoElements" : [ {
                "type" : "statictext",
                "size" : {
                    "width" : 190,
                    "height" : 14
                },
                "position" : {
                    "x" : 5,
                    "y" : 5
                },
                "text" : "OUT: ",
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "LabelStyle",
                    "description" : "Small label",
                    "background" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : true,
                        "color" : {
                            "r" : 169,
                            "g" : 169,
                            "b" : 169,
                            "a" : 1,
                            "rgb" : "rgb(169,169,169)",
                            "rgba" : "rgba(169,169,169,1.0)",
                            "hex" : "#a9a9a9"
                        },
                        "italic" : false,
                        "size" : 13
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-00005ee32029"
            }, {
                "type" : "textfield",
                "identifier" : "out",
                "textBody" : "...",
                "size" : {
                    "width" : 190,
                    "height" : 14
                },
                "position" : {
                    "x" : 5,
                    "y" : 22
                },
                "editable" : true,
                "multiline" : true,
                "align" : {
                    "horizontal" : "middle",
                    "vertical" : "middle"
                },
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "TextStyle",
                    "description" : "General text style",
                    "background" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : false,
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "italic" : false,
                        "size" : 14
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-000079606547"
            } ],
            "style" : {
                "name" : "BoxStyle",
                "description" : "A simple black/white box",
                "background" : {
                    "color" : {
                        "r" : 255,
                        "g" : 255,
                        "b" : 255,
                        "a" : 1,
                        "rgb" : "rgb(255,255,255)",
                        "rgba" : "rgba(255,255,255,1.0)",
                        "hex" : "#ffffff"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : false,
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "italic" : false,
                    "size" : 10
                },
                "line" : {
                    "color" : {
                        "r" : 169,
                        "g" : 169,
                        "b" : 169,
                        "a" : 1,
                        "rgb" : "rgb(169,169,169)",
                        "rgba" : "rgba(169,169,169,1.0)",
                        "hex" : "#a9a9a9"
                    },
                    "style" : "solid",
                    "width" : 1
                },
                "transparency" : 1
            },
            "id" : "00000000-0000-0000-0000-000062411837"
        } ]
    } ],
    "edges" : [ {
        "name" : "DropAnchor",
        "conceptElement" : "Anchor.dropAnchor",
        "target" : "Entity",
        "style" : {
            "name" : "default",
            "description" : "default",
            "background" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                }
            },
            "font" : {
                "name" : "Arial",
                "bold" : false,
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "italic" : false,
                "size" : 10
            },
            "line" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "style" : "solid",
                "width" : 1
            },
            "transparency" : 1
        },
        "placings" : [ {
            "style" : {
                "name" : "default",
                "description" : "default",
                "background" : {
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : false,
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "italic" : false,
                    "size" : 10
                },
                "line" : {
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "style" : "solid",
                    "width" : 1
                },
                "transparency" : 1
            },
            "position" : {
                "offset" : 1
            },
            "geoElement" : {
                "type" : "polyline",
                "points" : [ {
                    "x" : 0,
                    "y" : 8
                }, {
                    "x" : -12,
                    "y" : 0
                }, {
                    "x" : 0,
                    "y" : -8
                } ],
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "AnchorEdgeStyle",
                    "description" : "Style for an arrow edge anchor",
                    "background" : {
                        "color" : {
                            "r" : 255,
                            "g" : 255,
                            "b" : 255,
                            "a" : 1,
                            "rgb" : "rgb(255,255,255)",
                            "rgba" : "rgba(255,255,255,1.0)",
                            "hex" : "#ffffff"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : false,
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "italic" : false,
                        "size" : 10
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000ae0fd132"
            }
        } ],
        "meta" : null
    },{
        "name" : "LinkConnection",
       // "conceptElement" : "Anchor.dropAnchor", // not needed when meta is set
       // "target" : "Entity", // not needed when meta is set
        "style" : {
            "name" : "default",
            "description" : "default",
            "background" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                }
            },
            "font" : {
                "name" : "Arial",
                "bold" : false,
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "italic" : false,
                "size" : 10
            },
            "line" : {
                "color" : {
                    "r" : 0,
                    "g" : 0,
                    "b" : 0,
                    "a" : 1,
                    "rgb" : "rgb(0,0,0)",
                    "rgba" : "rgba(0,0,0,1.0)",
                    "hex" : "#000000"
                },
                "style" : "solid",
                "width" : 1
            },
            "transparency" : 1
        },
        "placings" : [ {
            "style" : {
                "name" : "default",
                "description" : "default",
                "background" : {
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    }
                },
                "font" : {
                    "name" : "Arial",
                    "bold" : false,
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "italic" : false,
                    "size" : 10
                },
                "line" : {
                    "color" : {
                        "r" : 0,
                        "g" : 0,
                        "b" : 0,
                        "a" : 1,
                        "rgb" : "rgb(0,0,0)",
                        "rgba" : "rgba(0,0,0,1.0)",
                        "hex" : "#000000"
                    },
                    "style" : "solid",
                    "width" : 1
                },
                "transparency" : 1
            },
            "position" : {
                "offset" : 1
            },
            "geoElement" : {
                "type" : "polyline",
                "points" : [ {
                    "x" : 0,
                    "y" : 8
                }, {
                    "x" : -12,
                    "y" : 0
                }, {
                    "x" : 0,
                    "y" : -8
                } ],
                "childGeoElements" : [ ],
                "style" : {
                    "name" : "AnchorEdgeStyle",
                    "description" : "Style for an arrow edge anchor",
                    "background" : {
                        "color" : {
                            "r" : 255,
                            "g" : 255,
                            "b" : 255,
                            "a" : 1,
                            "rgb" : "rgb(255,255,255)",
                            "rgba" : "rgba(255,255,255,1.0)",
                            "hex" : "#ffffff"
                        }
                    },
                    "font" : {
                        "name" : "Arial",
                        "bold" : false,
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "italic" : false,
                        "size" : 10
                    },
                    "line" : {
                        "color" : {
                            "r" : 0,
                            "g" : 0,
                            "b" : 0,
                            "a" : 1,
                            "rgb" : "rgb(0,0,0)",
                            "rgba" : "rgba(0,0,0,1.0)",
                            "hex" : "#000000"
                        },
                        "style" : "solid",
                        "width" : 1
                    },
                    "transparency" : 1
                },
                "id" : "00000000-0000-0000-0000-0000ae0fd132"
            }
        } ],
        meta: {
            source: {
                mclass: "Entity",
                mref: "links"
            },
            target: {
                mclass: "Entity",
                mref: "hasLink"
            },
            forMClass: "Link"
        }
    } ]
};