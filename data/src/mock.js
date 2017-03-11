const Generator = require('./generator');

const system = {
  name: 'system',
  password: 'superSecretPassword',
  admin_channels: ['ch-system', '*'],
  admin_roles: []
};

module.exports.users = [
  system
];

const options1 = {
  "$schema": "http://json-schema.org/schema#",
  "title": "Options",
  "type": "object",
  "properties": {
    "name": {
      "title": "Name",
      "type": "string",
      "required": true
    }
  }
};

const options2 = {
  "$schema": "http://json-schema.org/schema#",
  "title": "Options",
  "type": "object",
  "properties": {
    "name": {
      "title": "Name",
      "type": "string",
      "required": true
    },
    "metaModelRelease": {
      "$ref": "#/definitions/MetaModelRelease"
    }
  }
};

const options3 = {
  "$schema": "http://json-schema.org/schema#",
  "title": "Options",
  "type": "object",
  "properties": {
    "name": {
      "title": "Name",
      "type": "string",
      "required": true
    }
  }
};

const options4 = {
  "$schema": "http://json-schema.org/schema#",
  "title": "Options",
  "type": "object",
  "properties": {
    "name": {
      "title": "Name",
      "type": "string",
      "required": true
    }
  }
};


var list = new Generator(system);

list.
GeneratorImage('Basic Generator', 'modigen/generator/basic:0.1', 'Basic scala generator.', options1).
GeneratorImage('Specific Generator', 'modigen/generator/specific:0.1', 'Scala generator for a specific meta model.', options2).
GeneratorImage('Remote Demo', 'modigen/generator/remote:0.1', 'Scala generator which demonstrate the remote generator invocation.', options3).
GeneratorImage('File Demo', 'modigen/generator/file:0.1', 'Scala generator which demonstrate save to a file..', options4);


module.exports.docs = list.getDocs();
