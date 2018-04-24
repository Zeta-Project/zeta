//OLD
export var linkhelper = {

    placingTexts: {
        inheritance: {}, realization: {}, BaseClassRealization: {}, component: {}, aggregation: {}
    },


    getLabelText: function (edge, textId) {
        var text = this.placingTexts[edge][textId];
        if (text === undefined) {
            text = "";
        }
        return text;
    },


    mapping: {

        Inheritance: {}
        ,
        Realization: {}
        ,
        BaseClassRealization: {}
        ,
        Component: {}
        ,
        Aggregation: {}

    }

};



