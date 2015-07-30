var serverCommunication = {

	// ================== Config ==================
	config : {
		/**
		 * DEBUG:true will show debug information in the browser console.
		 */
		DEBUG : false,

		/**
		 * When REAL_TIME_COLLABORATION is true, every event will be sent to the
		 * server immediately. When it is set to false, the change will be sent
		 * when the changing has finished.
		 */
		REAL_TIME_COLLABORATION : true
	},

	// ================== Global Variables ==================
	global : {
		webSocketUri : "ws://" + window.location.host + "/socket/"
				+ window._global_graph_type + "/" + window._global_uuid,
		graphType : window._global_graph_type,
		userID : window._global_userID,
		userName : window._global_userName
	},

	// ================== Constants ==================
	constants : {
		// from jointjs.com/api
		NON_BATCH_EVENTS : [ "add", "remove",
				"change:source", "change:target", "change:attrs",
				"change:embeds", "change:parent", "change:z", "change:smooth",
				"change:manhattan" ]
	},

	// ================== Instance variables ==================
	socket : null,
	graph : null,
	paper : null,
	messageQueue : [],
	batchStarted : false,
	lastBatchEvent : null,
	lastBatchCells : [],

	// ================== Initialization ==================
	/**
	 * Has to be called once when starting the editor. This is the only "public"
	 * method in this class; the others will be triggered by messages or events.
	 */
	init : function(graph, paper) {
		this.graph = graph;
		this.paper = paper;

		userTags.init(paper);

		window.onbeforeunload = function() {
			serverCommunication.log("window.onbeforeunload");
			serverCommunication.closeSocket();
		};

		this.graph.on('all', this.onEvent, this);
		this.createSocket();
	},

	// ================== Event Handling ==================
	/**
	 * Is triggered, when a graph-event occurs.
	 */
	onEvent : function(eventName, cell, data, options) {
		//this.log("onEvent", eventName);

		if (options && options.remote) {
			return;
		}

		if (cell && cell.attributes && cell.attributes.type === "modigen.MLink") {
		    // only send link events to server if the link type was already determined
            if(cell.attributes.styleSet){
                // if link type was determined, set meta information to establish link
                // in ecore
                contextMenu.enhanceLinkForEcore(cell);

                // we omitted the initial add event, because the link type was not determined yet
                // so fake an add event once, to trigger creation of submapping on server
                if(!cell.attributes.added){
                     eventName = "add";
                     cell.attributes.added = true;
                }
            }else{
                return;
            }
		}

		if (this.config.REAL_TIME_COLLABORATION) {
			if (this.isGraphRelevant(eventName)) {
				this.notifyCellChanged(eventName, cell);
			}
		} else {
			this.processEventNoRealTime(eventName, cell);
		}
	},

	/**
	 * Handles sending the changed cells etc. if REAL_TIME_COLLABORATION is
	 * false. Batch-events will only be sent after batch:stop.
	 */
	processEventNoRealTime : function(eventName, cell) {
		this.log("processEventNoRealTime", eventName);

		if (!this.isBatchEvent(eventName) && this.isGraphRelevant(eventName)) {
			this.notifyCellChanged(eventName, cell);
			return;
		}

		switch (eventName) {

		case "batch:start":
			this.batchStarted = true;
			break;

		case "batch:stop":
			if (this.lastBatchEvent !== null && this.lastBatchCells.length > 0) {
				for (var i = 0; i < this.lastBatchCells.length; ++i) {
					this.notifyCellChanged(this.lastBatchEvent,
							this.lastBatchCells[i]);
				}
				this.lastBatchEvent = null;
				this.lastBatchCells = [];
			}

			this.batchStarted = false;
			break;

		default:
			if (this.isGraphRelevant(eventName)) {
				if (this.batchStarted) {
					this.lastBatchEvent = eventName;
					if (this.lastBatchCells.indexOf(cell) === -1) {
						this.lastBatchCells.push(cell);
					}
				} else {
					this.notifyCellChanged(eventName, cell);
				}
			}
			break;

		}
	},

	/**
	 * Is called after receiving a message. It "executes" the "instruction" from
	 * the message (like adding, removing or changing a cell, etc.).
	 */
	execute : function(message) {
		this.log("execute", message.type);

		if (message.error) {
			alert(message.error);
			window.location = "/";
			return;
		}

		// Not graph related messages
		switch (message.type) {
		case "userListUpdate":
			this.log("Got user update message");

			chat.replaceUserList(message.userList);
			return;

		case "chatMessage":
			this.log("Got chat message");
			chat.addToChatList(message);
			return;
		}

		var remoteCell = message.data.cell;

		switch (message.type) {

		case "cellChanged":
			switch (message.data.eventName) {

			case "add":
				this.graph.addCell(remoteCell, {
					remote : true
				});
				userTags.handleTagDrawing(this.getLocalCell(remoteCell), message.userID, message.userName);
				break;

			case "remove":
				var localCell = this.getLocalCell(remoteCell);
				if (localCell) {
					localCell.remove({
						disconnectLinks : true,
						remote : true
					});
				}
				break;

			default:
				var localCell = this.getLocalCell(remoteCell);
				if (localCell) {
					var attr = message.data.eventName.substr("change:".length);
					localCell.set(attr, message.data.cell[attr], {
						remote : true
					});
				}
				userTags.handleTagDrawing(this.getLocalCell(remoteCell), message.userID, message.userName);
				break;
			}
			break;

		case "getGraph":
			if (message.data.cells) {
				this.graph.fromJSON(message.data, {
					remote : true
				});
				// trigger own event, used in graphSvg
				this.paper.trigger("webSocketGraphLoaded");
			}
			break;
		}
	},

	// ================== Messages and notifying ==================
	/**
	 * Composes the message for a "cell has changed"-event and pushes it into
	 * the message queue.
	 */
	notifyCellChanged : function(eventName, cell) {
		this.log("notifyCellChanged", eventName);

		var ecoreName = cell.attributes.ecoreName;
		if (typeof ecoreName === 'undefined') {
			ecoreName = "";
		}

		var message = {
			type : "cellChanged",
			ecore : this.isEcoreRelevant(eventName),
			userID : this.global.userID,
			userName: this.global.userName,
			data : {
				eventName : eventName,
				ecoreName : ecoreName,
				cell : cell
			}
		};

		userTags.handleTagDrawing(cell, this.global.userID, this.global.userName);
		this.messageQueue.push(message);

		this.send();
	},

    /**
	 * Sends all the messages waiting to be sent from the messageQueue.
	 */
	send : function() {
		this.log("send");

		if (this.socket.readyState !== this.socket.OPEN) {
			return;
		}

		var notSent = [];

		for (var i = 0; i < this.messageQueue.length; ++i) {
			var message = this.messageQueue[i];
			var success = this.sendMessage(message);
			if (!success) {
				notSent.push(message);
			}
		}

		// best way to empty an array? http://davidwalsh.name/empty-array
		this.messageQueue.length = 0;

		for (i = 0; i < notSent.length; ++i) {
			this.messageQueue.push(notSent[i]);
		}

		if (this.messageQueue.length > 0) {
			this.send();
		}
	},

	/**
	 * Composes the message for getting the graph from the server and pushes it
	 * into the messageQueue.
	 */
	getGraph : function() {
		this.log("getGraph");

		var message = {
			type : "getGraph",
			ecore : false,
			userName : this.global.userName
		// announces that
		};

		this.messageQueue.push(message);

		this.send();
	},

	/**
	 * Converts a message to a JSON string and sends it via websocket to the
	 * server.
	 */
	sendMessage : function(message) {
		this.log("sendMessage");

		if (this.socket.readyState !== this.socket.OPEN) {
			return false;
		}

		this.socket.send(JSON.stringify(message));
		return true;
	},

	// ================== WebSocket ==================
	/**
	 * Initializes the websocket-connection.
	 */
	createSocket : function() {
		this.log("createSocket");
		this.socket = new WebSocket(this.global.webSocketUri);
		this.socket.onopen = this.onOpen;
		this.socket.onclose = this.onClose;
		this.socket.onerror = this.onError;
		this.socket.onmessage = this.onMessage;
	},

	socketConnectionLost : function() {
		this.log("socketConnectionLost");
		prompt(
				"Connection to WebSocket lost! Either there is an error in your internet connection, or the server is down. To access your graph after the error is resolved, keep the following link:",
				window.location);
		window.location = "/";
	},

	closeSocket : function() {
		this.log("closeSocket");
		this.socket.onclose = $.noop;
		this.socket.close();
	},

	/***************************************************************************
	 * ==================== Direct socket functions ====================
	 * (Keyword "this" refers to the socket, not to serverCommunication)
	 */

	onMessage : function(messageEvent) {
		serverCommunication.log("onMessage");
		var message = JSON.parse(messageEvent.data)
		serverCommunication.execute(message);
	},

	onOpen : function() {
		serverCommunication.log("onOpen");
		serverCommunication.getGraph();
	},

	onClose : function(event) {
		serverCommunication.log("onClose");
		serverCommunication.socketConnectionLost();
	},

	onError : function() {
		serverCommunication.log("onError");
		serverCommunication.socketConnectionLost();
	},

	// ================== Util Methods ==================

	/**
	 * Checks, if an event is relevant for the graph.
	 */
	isGraphRelevant : function(eventName) {
		this.log("isGraphRelevant", eventName);
		return eventName === "add" || eventName === "remove"
				|| eventName.substr(0, "change:".length) === "change:";
	},

	/**
	 * Checks, if an event is relevant to be saved in the Ecore.
	 */
	isEcoreRelevant : function(eventName) {
		this.log("isEcoreRelevant", eventName);
		return !(this.isBatchEvent(eventName));
	},

	/**
	 * Checks, if an event is executed in "batch"-mode. A batch-event is for
	 * example "change:position". => batch:start, change:position, batch:stop
	 */
	isBatchEvent : function(eventName) {
		return this.constants.NON_BATCH_EVENTS.indexOf(eventName) === -1;
	},

	/**
	 * Counts the 1st-level-keys of an object.
	 */
	countKeys : function(obj) {
		var count = 0;
		var key;
		for (key in obj) {
			if (obj.hasOwnProperty(key)) {
				++count;
			}
		}
		return count;
	},

	/**
	 * Returns the local cell for a remote cell.
	 */
	getLocalCell : function(remoteCell) {
		return this.graph.getCell(remoteCell.id);
	},

	/**
	 * Returns the cellView for a local cell.
	 */
	getCellView : function(localCell) {
		return this.paper.findViewByModel(localCell);
	},

	/**
	 * Wrapper for console.log, that checks if the class is in DEBUG mode and
	 * also does a little bit of styling.
	 */
	log : function(functionName, message) {
		if (this.config.DEBUG) {
			if (!message) {
				message = "";
			}
			console.log("[" + functionName + "] " + message);
		}
	},

	sendChatMessage : function(text) {
		this.log("sendChatMessage", text);
		var message = {
			type : "chatMessage",
			ecore : false,
			userName : this.global.userName,
			msg : text
		};
		this.messageQueue.push(message);
		this.send();

	}
};