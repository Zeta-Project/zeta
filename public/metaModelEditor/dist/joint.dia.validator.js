/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


joint.dia.Validator = Backbone.Model.extend({

    initialize: function(options) {

	this._map = {};
	this._commandManager = options.commandManager;

	this.listenTo(this._commandManager, 'add', this._onCommand);
    },

    defaults: {

	// To cancel (= undo + delete from redo stack) a command if is not valid.
	cancelInvalid: true
    },

    // iterates throught each command, stops on first invalid command.
    _onCommand: function(command) {

	return _.isArray(command)

	    ? _.find(command, function(singleCmd) { return !this._validateCommand(singleCmd); }, this)

	    : this._validateCommand(command);
    },

    // check whether the command is not against any rule
    _validateCommand: function(command) {

	// Backbone.model set() and Backbone.collection add() allow to pass an option parameter.
	// That is also kept within the command. It skips validation if requested.
	if (command.options && command.options.validation === false) return true;

	var handoverErr;

	_.each(this._map[command.action], function(route) {

	    var i = 0;

	    function callbacks(err) {

		var fn = route[i++];

		try {
		    if (fn) {
			fn(err, command, callbacks);
		    } else {
			handoverErr = err;
			return;
		    }
		} catch (err) {
		    callbacks(err);
		}
	    };

	    callbacks(handoverErr);
	});

	if (handoverErr) {

	    if (this.get('cancelInvalid')) this._commandManager.cancel();
	    this.trigger('invalid', handoverErr);
	    return false;
	}

	//command is valid
	return true;
    },


    validate: function(actions) {
	
	var callbacks = _.rest(arguments);

	_.each(callbacks, function(callback) {
	    if (_.isFunction(callback)) return;
	    throw new Error(actions + ' requires callback functions.');
	});

	_.each(actions.split(' '), function(action) {
	    (this._map[action] = this._map[action] || []).push(callbacks);
	}, this);

	return this;
    }

});

