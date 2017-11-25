import $ from 'jquery';
import './graphical-editor/graphical-editor';

import '../assets/icon-to-back.svg';
import '../assets/icon-to-front.svg';

import Backbone from 'backbone1.0';
import Main from './graphical-editor/meta-model';

new Main();
Backbone.history.start();

$('#metamodel-name').text("Metamodel: "+window.loadedMetaModel.name);
