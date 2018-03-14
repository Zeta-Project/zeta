//OLD

import { CommonInspectorInputs, CommonInspectorGroups, inp } from '../../../inspector';


export var InspectorDefs = {

    'zeta.klasse':{
        inputs: _.extend({
            attrs: {
                'text.90a7d93a-5efd-4c40-a90f-e74a9f76bfe3' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text3',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 3',
                        index: 1,
                        max: 190,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 3',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.90a7d93a-5efd-4c40-a90f-e74a9f76bfe3': inp({
                    'font-size': {
                        group: 'Text Style 3',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 3',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 3',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 3',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'rect.cc40a695-e82d-4ea7-b5e6-86ac2ae249bd': inp({

                    fill: {
                        group: 'Presentation Rectangle 3',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 3',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 3',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 3',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 3',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.cc40a695-e82d-4ea7-b5e6-86ac2ae249bd': inp({

                    x: {
                        group: 'Geometry Rectangle 3',
                        index: 1,
                        max: 0,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 3',
                        index: 2,
                        max: 150,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 3',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 3',
                        index: 3,
                        max: 200,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'rect.0b800d24-7d92-4fad-89df-3203d277fe4f': inp({

                    fill: {
                        group: 'Presentation Rectangle 2',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 2',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 2',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 2',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 2',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.0b800d24-7d92-4fad-89df-3203d277fe4f': inp({

                    x: {
                        group: 'Geometry Rectangle 2',
                        index: 1,
                        max: 0,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 2',
                        index: 2,
                        max: 200,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 2',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 2',
                        index: 3,
                        max: 200,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'rect.d62b0c84-6348-4cd3-b308-508d25012db9': inp({

                    fill: {
                        group: 'Presentation Rectangle 1',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 1',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 1',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 1',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 1',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.d62b0c84-6348-4cd3-b308-508d25012db9': inp({

                    x: {
                        group: 'Geometry Rectangle 1',
                        index: 1,
                        max: 0,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 1',
                        index: 2,
                        max: 150,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 1',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 1',
                        index: 3,
                        max: 200,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'text.e477df6c-e8da-462e-9dc3-2f88d830547f' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text1',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 1',
                        index: 1,
                        max: 190,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 1',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.e477df6c-e8da-462e-9dc3-2f88d830547f': inp({
                    'font-size': {
                        group: 'Text Style 1',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 1',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 1',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 1',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'text.ade81ec0-d7d0-44e0-ab85-0e6253c45bc1' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text2',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 2',
                        index: 1,
                        max: 190,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 2',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.ade81ec0-d7d0-44e0-ab85-0e6253c45bc1': inp({
                    'font-size': {
                        group: 'Text Style 2',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 2',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 2',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 2',
                        index: 6,
                        label: 'Text Color'
                    }
                })

            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'zeta.abstractKlasse':{
        inputs: _.extend({
            attrs: {

                'rect.f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb': inp({

                    fill: {
                        group: 'Presentation Rectangle 2',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 2',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 2',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 2',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 2',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb': inp({

                    x: {
                        group: 'Geometry Rectangle 2',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 2',
                        index: 2,
                        max: 200,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 2',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 2',
                        index: 3,
                        max: 210,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'text.60cee325-f76b-4d41-b08f-e51427aadf66' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text31',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 1',
                        index: 1,
                        max: 200,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 1',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.60cee325-f76b-4d41-b08f-e51427aadf66': inp({
                    'font-size': {
                        group: 'Text Style 1',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 1',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 1',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 1',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'text.0685d1f3-9273-42f9-b15f-34ea4a6be378' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text21',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 3',
                        index: 1,
                        max: 200,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 3',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.0685d1f3-9273-42f9-b15f-34ea4a6be378': inp({
                    'font-size': {
                        group: 'Text Style 3',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 3',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 3',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 3',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'text.b5762097-dfcf-41a9-8b11-2190c618e6e9' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text11',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 2',
                        index: 1,
                        max: 200,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 2',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.b5762097-dfcf-41a9-8b11-2190c618e6e9': inp({
                    'font-size': {
                        group: 'Text Style 2',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 2',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 2',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 2',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'rect.bd0fa679-b080-4d84-9eeb-fe7ae99a42cd': inp({

                    fill: {
                        group: 'Presentation Rectangle 1',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 1',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 1',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 1',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 1',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.bd0fa679-b080-4d84-9eeb-fe7ae99a42cd': inp({

                    x: {
                        group: 'Geometry Rectangle 1',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 1',
                        index: 2,
                        max: 150,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 1',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 1',
                        index: 3,
                        max: 210,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'rect.8586b658-768a-4273-b366-d4f1597c561e': inp({

                    fill: {
                        group: 'Presentation Rectangle 3',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 3',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 3',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 3',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 3',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.8586b658-768a-4273-b366-d4f1597c561e': inp({

                    x: {
                        group: 'Geometry Rectangle 3',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 3',
                        index: 2,
                        max: 150,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 3',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 3',
                        index: 3,
                        max: 210,
                        label: 'Width Rectangle'
                    }

                })

            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'zeta.interface':{
        inputs: _.extend({
            attrs: {

                'rect.75f2205e-9a60-45e9-9d33-98e84de80d66': inp({

                    fill: {
                        group: 'Presentation Rectangle 3',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 3',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 3',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 3',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 3',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.75f2205e-9a60-45e9-9d33-98e84de80d66': inp({

                    x: {
                        group: 'Geometry Rectangle 3',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 3',
                        index: 2,
                        max: 150,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 3',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 3',
                        index: 3,
                        max: 210,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'rect.9461a54c-fbb2-49a2-94ac-77848fbc1f88': inp({

                    fill: {
                        group: 'Presentation Rectangle 1',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 1',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 1',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 1',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 1',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.9461a54c-fbb2-49a2-94ac-77848fbc1f88': inp({

                    x: {
                        group: 'Geometry Rectangle 1',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 1',
                        index: 2,
                        max: 200,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 1',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 1',
                        index: 3,
                        max: 210,
                        label: 'Width Rectangle'
                    }

                })
                ,
                'text.418aa18b-d386-4d43-b74e-9b0701ef2dee' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text113',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 3',
                        index: 1,
                        max: 200,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 3',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.418aa18b-d386-4d43-b74e-9b0701ef2dee': inp({
                    'font-size': {
                        group: 'Text Style 3',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 3',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 3',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 3',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'text.2636f960-1374-46ab-b6a0-fc8e2cb3d80d' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text313',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 2',
                        index: 1,
                        max: 200,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 2',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.2636f960-1374-46ab-b6a0-fc8e2cb3d80d': inp({
                    'font-size': {
                        group: 'Text Style 2',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 2',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 2',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 2',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'text.5215ddbc-bcb1-414d-878c-4cea63c06ff5' : inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text213',
                        index: 1

                    },
                    x: {
                        group: 'Text Geometry 1',
                        index: 1,
                        max: 200,
                        label: 'x Position Text'
                    },
                    y: {
                        group: 'Text Geometry 1',
                        index: 2,
                        max: 210,
                        label: 'y Position Text'
                    }
                }),
                '.5215ddbc-bcb1-414d-878c-4cea63c06ff5': inp({
                    'font-size': {
                        group: 'Text Style 1',
                        index: 2
                    },
                    'font-family': {
                        group: 'Text Style 1',
                        index: 3
                    },
                    'font-weight': {
                        group: 'Text Style 1',
                        index: 4
                    },
                    fill: {
                        group: 'Text Style 1',
                        index: 6,
                        label: 'Text Color'
                    }
                })
                ,
                'rect.73e17224-4508-463a-a388-c299a5adde76': inp({

                    fill: {
                        group: 'Presentation Rectangle 2',
                        index: 1,
                        label: 'Background-Color Rectangle'
                    },
                    'fill-opacity': {
                        group: 'Presentation Rectangle 2',
                        index: 2,
                        label: 'Opacity Rectangle'
                    },
                    stroke: {
                        group: 'Presentation Rectangle 2',
                        index: 3,
                        label: 'Line-Color Rectangle'
                    },
                    'stroke-width': {
                        group: 'Presentation Rectangle 2',
                        index: 4,
                        min: 0,
                        max: 30,
                        defaultValue: 1
                    },
                    'stroke-dasharray': {
                        group: 'Presentation Rectangle 2',
                        index: 5,
                        label: 'Stroke Dash Rectangle'
                    }

                }),
                '.73e17224-4508-463a-a388-c299a5adde76': inp({

                    x: {
                        group: 'Geometry Rectangle 2',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 2',
                        index: 2,
                        max: 150,
                        label: 'y Position Rectangle'
                    },
                    height: {
                        group: 'Geometry Rectangle 2',
                        index: 3,
                        max: 250,
                        label: 'Height Rectangle'
                    },
                    width: {
                        group: 'Geometry Rectangle 2',
                        index: 3,
                        max: 210,
                        label: 'Width Rectangle'
                    }

                })

            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },


    'zeta.MLink': {
        inputs: {
            labels: {
                type: 'list',
                group: 'labels',
                attrs: {
                    label: {
                        'data-tooltip': 'Set (possibly multiple) labels for the link'
                    }
                },
                item: {

                    type: 'object',
                    properties: {
                        position: {
                            type: 'range',
                            min: 0.1,
                            max: .9,
                            step: .1,
                            defaultValue: .5,
                            label: 'position',
                            index: 2,
                            attrs: {
                                label: {
                                    'data-tooltip': 'Position the label relative to the source of the link'
                                }
                            }
                        },
                        attrs: {
                            text: {
                                text: {
                                    type: 'text',
                                    label: 'text',
                                    defaultValue: 'label',
                                    index: 1,
                                    attrs: {
                                        label: {
                                            'data-tooltip': 'Set text of the label'
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        },
        groups: {
            labels: {
                label: 'Labels',
                index: 1
            }
        }
    }

};

