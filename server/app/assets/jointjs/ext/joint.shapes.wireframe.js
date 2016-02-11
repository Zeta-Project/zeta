/*
 * This file defines all wireframe elements.
 * @author: Maximilian Göke
 */
joint.shapes.wireframe = {};


// ------------------------------------- Smartphone
joint.shapes.wireframe.SmartPhone = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><rect class="frame"/><rect class="display"/><circle class="homebtn"/></g></g>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.SmartPhone',

    size: { width: 50, height: 100},

    attrs: {
      '.frame': {fill: '#000000', width:50, height:100, rx: 5, ry: 5},
      '.display': {fill: '#FFFFFF', width:46, height:75, x: 2, y: 10},
      '.homebtn': {fill: '#FFFFFF',  cx:25, cy: 92, r:4}
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});

// ------------------------------------- Switch off
joint.shapes.wireframe.SwitchOff = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><rect class="wrapper"/><rect class="left"/></g><text/></g>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.SwitchOff',

    size: { width: 100, height: 20},

    attrs: {
      '.wrapper': {fill: '#7f7f7f', stroke: '#707070', width:100, height:20, 'stroke-width': 2, rx:5, ry:5},
      '.left': {fill: '#e7e7e7', width:40, height:20, rx:5, ry:5},
      text: { text: 'OFF', fill: '#FFFFFF', 'font-size': 18, 'ref-x': 0.6, 'ref-y': 0.5, 'y-alignment': 'middle', ref: '.wrapper'}
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});

// ------------------------------------- Switch on
joint.shapes.wireframe.SwitchOn = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><rect class="wrapper"/><rect class="right"/></g><text/></g>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.SwitchOn',

    size: { width: 100, height: 20},

    attrs: {
      '.wrapper': {fill: '#007fea', stroke: '#707070', width:100, height:20, 'stroke-width': 2, rx:5, ry:5},
      '.right': {fill: '#e7e7e7', width:40, height:20, x:60, rx:5, ry:5},
      text: { text: 'ON', fill: '#FFFFFF', 'font-size': 18, 'ref-x': 0.2, 'ref-y': 0.6, 'y-alignment': 'middle', ref: '.wrapper'}
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});

// ------------------------------------- Statusbar
joint.shapes.wireframe.Statusbar = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><rect class="background"/><rect class="service service1"/><rect class="service service2"/><rect class="service service3"/><rect class="service service4"/><rect class="service service5"/></g><text class="provider"/><text class="time"/></g>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.Statusbar',

    size: { width: 100, height: 20},

    attrs: {
      '.service': {fill: '#179cf9'},
      '.service1': {width: 1, height:6, x:2, y:11},
      '.service2': {width: 1, height:8, x:4, y:9},
      '.service3': {width: 1, height:10, x:6, y:7},
      '.service4': {width: 1, height:12, x:8, y:5},
      '.service5': {width: 1, height:14, x:10, y:3},
      '.provider': { text: 'HTWG', fill: '#000000', 'font-size': 8, 'ref-x': .15, 'ref-y': .5, 'y-alignment': 'middle', ref: '.background'},
      '.time': { text: '9:41 AM', fill: '#000000', 'font-size': 8, 'font-weight': 600, 'ref-x': .5, 'ref-y': .5, 'y-alignment': 'middle', 'x-alignment': 'middle', ref: '.background' },
      '.background': {fill: '#eaf2f5',width:100, height:20, rx:0, ry:0, 'stroke-width': 0}
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});

// ------------------------------------- Back Button
joint.shapes.wireframe.BackButton = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><polygon class="outer"/></g></g><text/>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.BackButton',

    size: { width: 100, height: 30},

    attrs: {
      polygon: { points: '0,15 10,0 100,0 100,30 10,30', fill: '#7d96b7'},
      'text': { 'font-size': 14, text: 'Back', 'ref-x': .15, 'ref-y': .5, 'y-alignment': 'middle', ref: '.outer', fill: '#FFFFFF', 'font-family': 'Arial, helvetica, sans-serif' }
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});

// ------------------------------------- Slider
joint.shapes.wireframe.Slider = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><rect/><circle/></g></g>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.Slider',

    size: { width: 100, height: 30},

    attrs: {
      rect: { stroke: '#c3c3c3', width: 100, height: 8, y: 10},
      circle: { stroke: '#aaaaaa',  cx:25, cy: 15, r:15}
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});

// ------------------------------------- Program Panel
joint.shapes.wireframe.ProgramPanel = joint.shapes.basic.Generic.extend({

  markup: '<g class="rotatable"><g class="scalable"><rect/></g></g><text/>',

  defaults: joint.util.deepSupplement({

    type: 'wireframe.ProgramPanel',

    size: { width: 100, height: 30},

    attrs: {
      rect: { fill: '#768ca9', width: 100, height: 30, rx: 0, ry:0, 'stroke-width': 0},
      'text': { 'font-size': 14, text: 'Program', 'ref-x': .5, 'ref-y': .5, 'y-alignment': 'middle', 'x-alignment': 'middle', ref: 'rect', fill: '#FFFFFF', 'font-family': 'Arial, helvetica, sans-serif' }
    }
  }, joint.shapes.basic.Generic.prototype.defaults)
});
