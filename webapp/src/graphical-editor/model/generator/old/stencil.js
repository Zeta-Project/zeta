/* * This is a generated stencil file for JointJS */
Stencil.groups = {anchors: {index: 2, label: 'Anchors'}, entity: {index: 1, label: 'Entity'}};
var
    entity = new joint.shapes.zeta.entity({
        nodeName: 'entity',
        mClass: 'Entity',
        mClassAttributeInfo: [{name: 'fix', type: 'StringType'}, {name: 'in', type: 'StringType'}, {name: 'out', type: 'StringType'}]
    })
;var
    periodStart = new joint.shapes.zeta.periodStart({
        nodeName: 'periodStart',
        mClass: 'PeriodStart',
        mClassAttributeInfo: []
    })
;var
    teamStart = new joint.shapes.zeta.teamStart({
        nodeName: 'teamStart',
        mClass: 'TeamStart',
        mClassAttributeInfo: []
    })
;Stencil.shapes = {
    entity: [
        entity
    ], anchors: [
        periodStart,
        teamStart
    ]
}
;$(document).ready(function () {
    entity.attr(getShapeStyle('entity'));
    periodStart.attr(getShapeStyle('periodStart'));
    teamStart.attr(getShapeStyle('teamStart'));
    var style = document.createElement('style');
    style.id = 'highlighting-style';
    style.type = 'text/css';
    style.innerHTML = getDiagramHighlighting("Default");
    document.getElementsByTagName('head')[0].appendChild(style);
});
