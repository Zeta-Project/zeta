//OLD

import { CommonInspectorInputs, CommonInspectorGroups, inp } from '../../../inspector';

export var InspectorDefs = {

    'zeta.klasse': {
        inputs: _.extend({
            attrs: {

                'text.5c264f7b-4821-4dd5-9798-70c44b8333ab': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text1',
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
                '.5c264f7b-4821-4dd5-9798-70c44b8333ab': inp({
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
                'text.5d37c65b-879b-41a9-9afa-391b81d89368': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text2',
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
                '.5d37c65b-879b-41a9-9afa-391b81d89368': inp({
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
                'rect.7b5ada00-b651-45cf-a008-915ecd505140': inp({

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
                '.7b5ada00-b651-45cf-a008-915ecd505140': inp({

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
                'text.76794e3f-0e79-4f5f-8bf2-c84ba07327e0': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text3',
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
                '.76794e3f-0e79-4f5f-8bf2-c84ba07327e0': inp({
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
                'rect.5c487482-2943-4980-8b65-b14498c354ba': inp({

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
                '.5c487482-2943-4980-8b65-b14498c354ba': inp({

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
                'rect.f48cfa94-c651-4ff2-a2bb-cb3eec43d065': inp({

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
                '.f48cfa94-c651-4ff2-a2bb-cb3eec43d065': inp({

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

            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'zeta.abstractKlasse': {
        inputs: _.extend({
            attrs: {

                'rect.b39e7aa2-a142-4dea-9013-52bd0eab784d': inp({

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
                '.b39e7aa2-a142-4dea-9013-52bd0eab784d': inp({

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
                'rect.163911d7-57f3-4f45-a434-e6d431025885': inp({

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
                '.163911d7-57f3-4f45-a434-e6d431025885': inp({

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
                'rect.1780c339-5031-4181-9aa6-53294cfa4310': inp({

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
                '.1780c339-5031-4181-9aa6-53294cfa4310': inp({

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
                'text.8ea01922-fbdf-4671-8bce-ce10658c49da': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text11',
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
                '.8ea01922-fbdf-4671-8bce-ce10658c49da': inp({
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
                'text.3324620f-e3be-4809-b404-c46be93ece5a': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text21',
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
                '.3324620f-e3be-4809-b404-c46be93ece5a': inp({
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
                'text.7f1a0b19-b973-494a-9f99-cf52011eab4d': inp({
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
                '.7f1a0b19-b973-494a-9f99-cf52011eab4d': inp({
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

            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'zeta.interface': {
        inputs: _.extend({
            attrs: {

                'text.f01bed45-534c-40c2-a17e-094afb22fc9d': inp({
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
                '.f01bed45-534c-40c2-a17e-094afb22fc9d': inp({
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
                'text.436e2eb9-27e7-493d-a527-fc1bf54616a6': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text313',
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
                '.436e2eb9-27e7-493d-a527-fc1bf54616a6': inp({
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
                'text.94cca154-a761-4824-9cc2-bb20a53e6cf9': inp({
                    text: {
                        type: 'list',
                        item: {
                            type: 'text'
                        },
                        group: 'Text text213',
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
                '.94cca154-a761-4824-9cc2-bb20a53e6cf9': inp({
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
                'rect.d89d5ff7-f9f1-4816-aa59-8d5df9c27424': inp({

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
                '.d89d5ff7-f9f1-4816-aa59-8d5df9c27424': inp({

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
                'rect.1d879b3a-cfa3-4955-99ff-6af1f5bf2ced': inp({

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
                '.1d879b3a-cfa3-4955-99ff-6af1f5bf2ced': inp({

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
                ,
                'rect.9f393e03-e371-41a9-8065-a62b13991229': inp({

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
                '.9f393e03-e371-41a9-8065-a62b13991229': inp({

                    x: {
                        group: 'Geometry Rectangle 3',
                        index: 1,
                        max: 10,
                        label: 'x Position Rectangle'
                    },
                    y: {
                        group: 'Geometry Rectangle 3',
                        index: 2,
                        max: 200,
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
