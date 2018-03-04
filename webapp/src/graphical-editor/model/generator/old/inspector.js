var
    InspectorDefs = {
        'zeta.entity':
            {
                inputs: _.extend({
                        attrs: {
                            'rect.bdb2b0bf-32a3-4f1b-ab6f-1d1d3435602c':
                                inp({
                                    fill: {
                                        group: 'Presentation Rectangle 4',
                                        index: 1,
                                        label: 'Background-Color Rectangle'
                                    }
                                    ,
                                    'fill-opacity':
                                        {
                                            group: 'Presentation Rectangle 4',
                                            index: 2,
                                            label: 'Opacity Rectangle'
                                        }
                                    ,
                                    stroke: {
                                        group: 'Presentation Rectangle 4',
                                        index: 3,
                                        label: 'Line-Color Rectangle'
                                    }
                                    ,
                                    'stroke-width':
                                        {
                                            group: 'Presentation Rectangle 4',
                                            index: 4,
                                            min: 0,
                                            max: 30,
                                            defaultValue: 1
                                        }
                                    ,
                                    'stroke-dasharray':
                                        {
                                            group: 'Presentation Rectangle 4',
                                            index: 5,
                                            label: 'Stroke Dash Rectangle'
                                        }
                                }),
                            '.bdb2b0bf-32a3-4f1b-ab6f-1d1d3435602c':
                                inp({
                                    x: {
                                        group: 'Geometry Rectangle 4',
                                        index: 1,
                                        max: 0,
                                        label: 'x Position Rectangle'
                                    }
                                    ,
                                    y: {
                                        group: 'Geometry Rectangle 4',
                                        index: 2,
                                        max: 300,
                                        label: 'y Position Rectangle'
                                    }
                                    ,
                                    height: {
                                        group: 'Geometry Rectangle 4',
                                        index: 3,
                                        max: 350,
                                        label: 'Height Rectangle'
                                    }
                                    ,
                                    width: {
                                        group: 'Geometry Rectangle 4',
                                        index: 3,
                                        max: 200,
                                        label: 'Width Rectangle'
                                    }
                                }),
                            'text.04e89195-b013-49bd-a7ee-b90571b87d9a':
                                inp({
                                    text: {
                                        type: 'list',
                                        item: {
                                            type: 'text'
                                        }
                                        ,
                                        group: 'Text entityName',
                                        index: 1
                                    }
                                    ,
                                    x: {
                                        group: 'Text Geometry 3',
                                        index: 1,
                                        max: 190,
                                        label: 'x Position Text'
                                    }
                                    ,
                                    y: {
                                        group: 'Text Geometry 3',
                                        index: 2,
                                        max: 310,
                                        label: 'y Position Text'
                                    }
                                }),
                            '.04e89195-b013-49bd-a7ee-b90571b87d9a':
                                inp({
                                    'font-size':
                                        {
                                            group: 'Text Style 3',
                                            index: 2
                                        }
                                    ,
                                    'font-family':
                                        {
                                            group: 'Text Style 3',
                                            index: 3
                                        }
                                    ,
                                    'font-weight':
                                        {
                                            group: 'Text Style 3',
                                            index: 4
                                        }
                                    ,
                                    fill: {
                                        group: 'Text Style 3',
                                        index: 6,
                                        label: 'Text Color'
                                    }
                                }),
                            'rect.832c988f-f4b8-436f-9c44-dd9f09699fa4':
                                inp({
                                    fill: {
                                        group: 'Presentation Rectangle 1',
                                        index: 1,
                                        label: 'Background-Color Rectangle'
                                    }
                                    ,
                                    'fill-opacity':
                                        {
                                            group: 'Presentation Rectangle 1',
                                            index: 2,
                                            label: 'Opacity Rectangle'
                                        }
                                    ,
                                    stroke: {
                                        group: 'Presentation Rectangle 1',
                                        index: 3,
                                        label: 'Line-Color Rectangle'
                                    }
                                    ,
                                    'stroke-width':
                                        {
                                            group: 'Presentation Rectangle 1',
                                            index: 4,
                                            min: 0,
                                            max: 30,
                                            defaultValue: 1
                                        }
                                    ,
                                    'stroke-dasharray':
                                        {
                                            group: 'Presentation Rectangle 1',
                                            index: 5,
                                            label: 'Stroke Dash Rectangle'
                                        }
                                }),
                            '.832c988f-f4b8-436f-9c44-dd9f09699fa4':
                                inp({
                                    x: {
                                        group: 'Geometry Rectangle 1',
                                        index: 1,
                                        max: 0,
                                        label: 'x Position Rectangle'
                                    }
                                    ,
                                    y: {
                                        group: 'Geometry Rectangle 1',
                                        index: 2,
                                        max: 250,
                                        label: 'y Position Rectangle'
                                    }
                                    ,
                                    height: {
                                        group: 'Geometry Rectangle 1',
                                        index: 3,
                                        max: 350,
                                        label: 'Height Rectangle'
                                    }
                                    ,
                                    width: {
                                        group: 'Geometry Rectangle 1',
                                        index: 3,
                                        max: 200,
                                        label: 'Width Rectangle'
                                    }
                                }),
                            'text.f72584e6-c4cc-4bb9-beeb-ef4cf3d40396':
                                inp({
                                    text: {
                                        type: 'list',
                                        item: {
                                            type: 'text'
                                        }
                                        ,
                                        group: 'Text inValues',
                                        index: 1
                                    }
                                    ,
                                    x: {
                                        group: 'Text Geometry 4',
                                        index: 1,
                                        max: 190,
                                        label: 'x Position Text'
                                    }
                                    ,
                                    y: {
                                        group: 'Text Geometry 4',
                                        index: 2,
                                        max: 310,
                                        label: 'y Position Text'
                                    }
                                }),
                            '.f72584e6-c4cc-4bb9-beeb-ef4cf3d40396':
                                inp({
                                    'font-size':
                                        {
                                            group: 'Text Style 4',
                                            index: 2
                                        }
                                    ,
                                    'font-family':
                                        {
                                            group: 'Text Style 4',
                                            index: 3
                                        }
                                    ,
                                    'font-weight':
                                        {
                                            group: 'Text Style 4',
                                            index: 4
                                        }
                                    ,
                                    fill: {
                                        group: 'Text Style 4',
                                        index: 6,
                                        label: 'Text Color'
                                    }
                                }),
                            'rect.708f3a8f-96e2-4a02-967f-fa26a952ca80':
                                inp({
                                    fill: {
                                        group: 'Presentation Rectangle 2',
                                        index: 1,
                                        label: 'Background-Color Rectangle'
                                    }
                                    ,
                                    'fill-opacity':
                                        {
                                            group: 'Presentation Rectangle 2',
                                            index: 2,
                                            label: 'Opacity Rectangle'
                                        }
                                    ,
                                    stroke: {
                                        group: 'Presentation Rectangle 2',
                                        index: 3,
                                        label: 'Line-Color Rectangle'
                                    }
                                    ,
                                    'stroke-width':
                                        {
                                            group: 'Presentation Rectangle 2',
                                            index: 4,
                                            min: 0,
                                            max: 30,
                                            defaultValue: 1
                                        }
                                    ,
                                    'stroke-dasharray':
                                        {
                                            group: 'Presentation Rectangle 2',
                                            index: 5,
                                            label: 'Stroke Dash Rectangle'
                                        }
                                }),
                            '.708f3a8f-96e2-4a02-967f-fa26a952ca80':
                                inp({
                                    x: {
                                        group: 'Geometry Rectangle 2',
                                        index: 1,
                                        max: 0,
                                        label: 'x Position Rectangle'
                                    }
                                    ,
                                    y: {
                                        group: 'Geometry Rectangle 2',
                                        index: 2,
                                        max: 250,
                                        label: 'y Position Rectangle'
                                    }
                                    ,
                                    height: {
                                        group: 'Geometry Rectangle 2',
                                        index: 3,
                                        max: 350,
                                        label: 'Height Rectangle'
                                    }
                                    ,
                                    width: {
                                        group: 'Geometry Rectangle 2',
                                        index: 3,
                                        max: 200,
                                        label: 'Width Rectangle'
                                    }
                                }),
                            'rect.3142e6f7-d844-430b-a13f-122530c56862':
                                inp({
                                    fill: {
                                        group: 'Presentation Rectangle 3',
                                        index: 1,
                                        label: 'Background-Color Rectangle'
                                    }
                                    ,
                                    'fill-opacity':
                                        {
                                            group: 'Presentation Rectangle 3',
                                            index: 2,
                                            label: 'Opacity Rectangle'
                                        }
                                    ,
                                    stroke: {
                                        group: 'Presentation Rectangle 3',
                                        index: 3,
                                        label: 'Line-Color Rectangle'
                                    }
                                    ,
                                    'stroke-width':
                                        {
                                            group: 'Presentation Rectangle 3',
                                            index: 4,
                                            min: 0,
                                            max: 30,
                                            defaultValue: 1
                                        }
                                    ,
                                    'stroke-dasharray':
                                        {
                                            group: 'Presentation Rectangle 3',
                                            index: 5,
                                            label: 'Stroke Dash Rectangle'
                                        }
                                }),
                            '.3142e6f7-d844-430b-a13f-122530c56862':
                                inp({
                                    x: {
                                        group: 'Geometry Rectangle 3',
                                        index: 1,
                                        max: 0,
                                        label: 'x Position Rectangle'
                                    }
                                    ,
                                    y: {
                                        group: 'Geometry Rectangle 3',
                                        index: 2,
                                        max: 250,
                                        label: 'y Position Rectangle'
                                    }
                                    ,
                                    height: {
                                        group: 'Geometry Rectangle 3',
                                        index: 3,
                                        max: 350,
                                        label: 'Height Rectangle'
                                    }
                                    ,
                                    width: {
                                        group: 'Geometry Rectangle 3',
                                        index: 3,
                                        max: 200,
                                        label: 'Width Rectangle'
                                    }
                                }),
                            'text.99f3e4be-728d-4ddf-a1e6-65df237a8913':
                                inp({
                                    text: {
                                        type: 'list',
                                        item: {
                                            type: 'text'
                                        }
                                        ,
                                        group: 'Text fixValues',
                                        index: 1
                                    }
                                    ,
                                    x: {
                                        group: 'Text Geometry 1',
                                        index: 1,
                                        max: 190,
                                        label: 'x Position Text'
                                    }
                                    ,
                                    y: {
                                        group: 'Text Geometry 1',
                                        index: 2,
                                        max: 310,
                                        label: 'y Position Text'
                                    }
                                }),
                            '.99f3e4be-728d-4ddf-a1e6-65df237a8913':
                                inp({
                                    'font-size':
                                        {
                                            group: 'Text Style 1',
                                            index: 2
                                        }
                                    ,
                                    'font-family':
                                        {
                                            group: 'Text Style 1',
                                            index: 3
                                        }
                                    ,
                                    'font-weight':
                                        {
                                            group: 'Text Style 1',
                                            index: 4
                                        }
                                    ,
                                    fill: {
                                        group: 'Text Style 1',
                                        index: 6,
                                        label: 'Text Color'
                                    }
                                }),
                            'text.9c2877f9-21bd-49d4-963e-ac2868be66c7':
                                inp({
                                    text: {
                                        type: 'list',
                                        item: {
                                            type: 'text'
                                        }
                                        ,
                                        group: 'Text outValues',
                                        index: 1
                                    }
                                    ,
                                    x: {
                                        group: 'Text Geometry 2',
                                        index: 1,
                                        max: 190,
                                        label: 'x Position Text'
                                    }
                                    ,
                                    y: {
                                        group: 'Text Geometry 2',
                                        index: 2,
                                        max: 310,
                                        label: 'y Position Text'
                                    }
                                }),
                            '.9c2877f9-21bd-49d4-963e-ac2868be66c7':
                                inp({
                                    'font-size':
                                        {
                                            group: 'Text Style 2',
                                            index: 2
                                        }
                                    ,
                                    'font-family':
                                        {
                                            group: 'Text Style 2',
                                            index: 3
                                        }
                                    ,
                                    'font-weight':
                                        {
                                            group: 'Text Style 2',
                                            index: 4
                                        }
                                    ,
                                    fill: {
                                        group: 'Text Style 2',
                                        index: 6,
                                        label: 'Text Color'
                                    }
                                })
                        }
                    },
                    CommonInspectorInputs
                ),
                groups: CommonInspectorGroups
            }
        ,
        'zeta.periodStart':
            {
                inputs: _.extend({
                        attrs: {
                            'ellipse.c52e2e13-0c39-4276-84c0-256147f4f1c4':
                                inp({
                                    fill: {
                                        group: 'Presentation Ellipse 1',
                                        index: 1,
                                        label: 'Background-Color Ellipse'
                                    }
                                    ,
                                    'fill-opacity':
                                        {
                                            group: 'Presentation Ellipse 1',
                                            index: 2,
                                            label: 'Opacity Ellipse'
                                        }
                                    ,
                                    stroke: {
                                        group: 'Presentation Ellipse 1',
                                        index: 3,
                                        label: 'Line-Color Ellipse'
                                    }
                                    ,
                                    'stroke-width':
                                        {
                                            group: 'Presentation Ellipse 1',
                                            index: 4,
                                            min: 0,
                                            max: 30,
                                            defaultValue: 1,
                                            label: 'Stroke Width Ellipse'
                                        }
                                    ,
                                    'stroke-dasharray':
                                        {
                                            group: 'Presentation Ellipse 1',
                                            index: 5,
                                            label: 'Stroke Dash Ellipse'
                                        }
                                }),
                            '.c52e2e13-0c39-4276-84c0-256147f4f1c4':
                                inp({
                                    cx: {
                                        group: 'Geometry Ellipse 1',
                                        index: 1,
                                        min: 40,
                                        max: 40
                                    }
                                    ,
                                    cy: {
                                        group: 'Geometry Ellipse 1',
                                        index: 2,
                                        min: 40,
                                        max: 40
                                    }
                                    ,
                                    rx: {
                                        group: 'Geometry Ellipse 1',
                                        index: 3,
                                        max: 40
                                    }
                                    ,
                                    ry: {
                                        group: 'Geometry Ellipse 1',
                                        index: 3,
                                        max: 40
                                    }
                                }),
                            'text.0946fd95-dec8-4e8a-85e3-02b16c2b09e4':
                                inp({
                                    text: {
                                        type: 'list',
                                        item: {
                                            type: 'text'
                                        }
                                        ,
                                        group: 'Text textPeriodStart',
                                        index: 1
                                    }
                                    ,
                                    x: {
                                        group: 'Text Geometry 1',
                                        index: 1,
                                        max: 70,
                                        label: 'x Position Text'
                                    }
                                    ,
                                    y: {
                                        group: 'Text Geometry 1',
                                        index: 2,
                                        max: 40,
                                        label: 'y Position Text'
                                    }
                                }),
                            '.0946fd95-dec8-4e8a-85e3-02b16c2b09e4':
                                inp({
                                    'font-size':
                                        {
                                            group: 'Text Style 1',
                                            index: 2
                                        }
                                    ,
                                    'font-family':
                                        {
                                            group: 'Text Style 1',
                                            index: 3
                                        }
                                    ,
                                    'font-weight':
                                        {
                                            group: 'Text Style 1',
                                            index: 4
                                        }
                                    ,
                                    fill: {
                                        group: 'Text Style 1',
                                        index: 6,
                                        label: 'Text Color'
                                    }
                                })
                        }
                    },
                    CommonInspectorInputs
                ),
                groups: CommonInspectorGroups
            }
        ,
        'zeta.teamStart':
            {
                inputs: _.extend({
                        attrs: {
                            'ellipse.5370cce1-af7a-43d2-bb82-686cc277c9f3':
                                inp({
                                    fill: {
                                        group: 'Presentation Ellipse 1',
                                        index: 1,
                                        label: 'Background-Color Ellipse'
                                    }
                                    ,
                                    'fill-opacity':
                                        {
                                            group: 'Presentation Ellipse 1',
                                            index: 2,
                                            label: 'Opacity Ellipse'
                                        }
                                    ,
                                    stroke: {
                                        group: 'Presentation Ellipse 1',
                                        index: 3,
                                        label: 'Line-Color Ellipse'
                                    }
                                    ,
                                    'stroke-width':
                                        {
                                            group: 'Presentation Ellipse 1',
                                            index: 4,
                                            min: 0,
                                            max: 30,
                                            defaultValue: 1,
                                            label: 'Stroke Width Ellipse'
                                        }
                                    ,
                                    'stroke-dasharray':
                                        {
                                            group: 'Presentation Ellipse 1',
                                            index: 5,
                                            label: 'Stroke Dash Ellipse'
                                        }
                                }),
                            '.5370cce1-af7a-43d2-bb82-686cc277c9f3':
                                inp({
                                    cx: {
                                        group: 'Geometry Ellipse 1',
                                        index: 1,
                                        min: 40,
                                        max: 40
                                    }
                                    ,
                                    cy: {
                                        group: 'Geometry Ellipse 1',
                                        index: 2,
                                        min: 40,
                                        max: 40
                                    }
                                    ,
                                    rx: {
                                        group: 'Geometry Ellipse 1',
                                        index: 3,
                                        max: 40
                                    }
                                    ,
                                    ry: {
                                        group: 'Geometry Ellipse 1',
                                        index: 3,
                                        max: 40
                                    }
                                }),
                            'text.949d3660-0b0a-4671-ac55-14d3acc32dcd':
                                inp({
                                    text: {
                                        type: 'list',
                                        item: {
                                            type: 'text'
                                        }
                                        ,
                                        group: 'Text textTeamStart',
                                        index: 1
                                    }
                                    ,
                                    x: {
                                        group: 'Text Geometry 1',
                                        index: 1,
                                        max: 70,
                                        label: 'x Position Text'
                                    }
                                    ,
                                    y: {
                                        group: 'Text Geometry 1',
                                        index: 2,
                                        max: 40,
                                        label: 'y Position Text'
                                    }
                                }),
                            '.949d3660-0b0a-4671-ac55-14d3acc32dcd':
                                inp({
                                    'font-size':
                                        {
                                            group: 'Text Style 1',
                                            index: 2
                                        }
                                    ,
                                    'font-family':
                                        {
                                            group: 'Text Style 1',
                                            index: 3
                                        }
                                    ,
                                    'font-weight':
                                        {
                                            group: 'Text Style 1',
                                            index: 4
                                        }
                                    ,
                                    fill: {
                                        group: 'Text Style 1',
                                        index: 6,
                                        label: 'Text Color'
                                    }
                                })
                        }
                    },
                    CommonInspectorInputs
                ),
                groups: CommonInspectorGroups
            }
        ,
        'zeta.MLink':
            {
                inputs: {
                    labels: {
                        type: 'list',
                        group: 'labels',
                        attrs: {
                            label: {
                                'data-tooltip':
                                    'Set (possibly multiple) labels for the link'
                            }
                        }
                        ,
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
                                            'data-tooltip':
                                                'Position the label relative to the source of the link'
                                        }
                                    }
                                }
                                ,
                                attrs: {
                                    text: {
                                        text: {
                                            type: 'text',
                                            label: 'text',
                                            defaultValue: 'label',
                                            index: 1,
                                            attrs: {
                                                label: {
                                                    'data-tooltip':
                                                        'Set text of the label'
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ,
                groups: {
                    labels: {
                        label: 'Labels',
                        index: 1
                    }
                }
            }
    }
;