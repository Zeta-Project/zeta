import $ from 'jquery';
import './graphical-editor/graphical-editor';
import './graphical-editor/model/ext/LinkView';
import './graphical-editor/model/ext/jointVElementText';

import '../assets/icon-to-back.svg';
import '../assets/icon-to-front.svg';

import './graphical-editor/model/css/halo.css';
import { MLink, MLinkView } from './graphical-editor/model/ext/zeta.link';
import './graphical-editor/model/ext/modelValidator';

import joint from 'jointjs';
import Backbone from 'backbone1.0';
import chat from './graphical-editor/model/ext/chat';
import { CommonInspectorInputs, CommonInspectorGroups, inp } from './graphical-editor/model/inspector';
import Main from './graphical-editor/model';
import GeneratorFactory from './graphical-editor/model/generator/GeneratorFactory';

$(document).ready(function() {
    joint.shapes.zeta.MLink = MLink;
    joint.shapes.zeta.MLinkView = MLinkView;
    GeneratorFactory.initialize().then(() => {
        new Main();
        Backbone.history.start();
    })
});

global.joint = joint;
global.chat = chat;
global.CommonInspectorInputs = CommonInspectorInputs;
global.CommonInspectorGroups = CommonInspectorGroups;
global.inp = inp;
global.globalMClassAttributeInfo = [];
global.globalMReferenceAttributeInfo = {};
global.attributePositionMarker = {};


