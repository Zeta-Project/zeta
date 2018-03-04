function getShapeStyle(elementName) {
    var style = {};
    switch (elementName) {

        case "entity":
            style['rect.bdb2b0bf-32a3-4f1b-ab6f-1d1d3435602c'] = getStyle('(child_of -> DefaultDefault)');
            style['text.04e89195-b013-49bd-a7ee-b90571b87d9a'] = getStyle('(child_of -> DefaultDefault)').text;
            style['.04e89195-b013-49bd-a7ee-b90571b87d9a'] = getStyle('(child_of -> DefaultDefault)').text;
            style['rect.3142e6f7-d844-430b-a13f-122530c56862'] = getStyle('(child_of -> DefaultDefault)');
            style['text.99f3e4be-728d-4ddf-a1e6-65df237a8913'] = getStyle('(child_of -> DefaultDefault)').text;
            style['.99f3e4be-728d-4ddf-a1e6-65df237a8913'] = getStyle('(child_of -> DefaultDefault)').text;
            style['rect.708f3a8f-96e2-4a02-967f-fa26a952ca80'] = getStyle('(child_of -> DefaultDefault)');
            style['text.f72584e6-c4cc-4bb9-beeb-ef4cf3d40396'] = getStyle('(child_of -> DefaultDefault)').text;
            style['.f72584e6-c4cc-4bb9-beeb-ef4cf3d40396'] = getStyle('(child_of -> DefaultDefault)').text;
            style['rect.832c988f-f4b8-436f-9c44-dd9f09699fa4'] = getStyle('(child_of -> DefaultDefault)');
            style['text.9c2877f9-21bd-49d4-963e-ac2868be66c7'] = getStyle('(child_of -> DefaultDefault)').text;
            style['.9c2877f9-21bd-49d4-963e-ac2868be66c7'] = getStyle('(child_of -> DefaultDefault)').text;
            break;
        case "periodStart":
            style['ellipse.c52e2e13-0c39-4276-84c0-256147f4f1c4'] = getStyle('(child_of -> Default & Red)');
            style['text.0946fd95-dec8-4e8a-85e3-02b16c2b09e4'] = getStyle('(child_of -> Default & Red)').text;
            style['.0946fd95-dec8-4e8a-85e3-02b16c2b09e4'] = getStyle('(child_of -> Default & Red)').text;
            break;
        case "teamStart":
            style['ellipse.5370cce1-af7a-43d2-bb82-686cc277c9f3'] = getStyle('(child_of -> Default & Blue)');
            style['text.949d3660-0b0a-4671-ac55-14d3acc32dcd'] = getStyle('(child_of -> Default & Blue)').text;
            style['.949d3660-0b0a-4671-ac55-14d3acc32dcd'] = getStyle('(child_of -> Default & Blue)').text;
            break;
        default:
            style = {};
            break;
    }
    return style;
}