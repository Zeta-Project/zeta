/**
 * Handle the direct collaboration between multiple clients which are connected to the same Meta Model.
 * The init function has to be called once. After the initialization, all functions will be triggered by
 * events from the JointJS graph or messages from the WebSocket.
 */
var collaboration = (function collaboration() {
    'use strict';

    var config = {
        debug: false,
        realTime: true,
        webSocketUri: "ws://" + window.location.host + "/metamodel/socket/" + window.loadedMetaModel.uuid,
        nonBatchEvents: ["add", "remove", "change:source", "change:target", "change:attrs", "change:embeds", "change:parent", "change:z", "change:smooth", "change:manhattan"]
    };

    var graph = null;
    var webSocket = null;

    var waitingForGraph = false;

    var messageQueue = [];

    var batchStarted = false;
    var batchEvent = null;
    var batchCells = [];

    log(collaboration.name, "Collaboration started");

    /**
     * Initialize the collaboration module.
     * Call it exactly once when starting the application.
     *
     * @param {joint.dia.Graph} _graph - The JointJS Graph.
     */
    function init(_graph) {
        log(init.name, "Initialized");

        graph = _graph;
        webSocket = createWebSocket();
        graph.on("all", function (eventName, cell, data, options) {
            onGraphEvent(eventName, cell, options);
        });
    }

    /**
     * Create the WebSocket with the configured webSocketUri.
     *
     * @returns {WebSocket} The WebSocket.
     */
    function createWebSocket() {
        log(createWebSocket.name);

        var webSocket = new WebSocket(config.webSocketUri);
        webSocket.onopen = onSocketOpen;
        webSocket.onclose = onSocketClose;
        webSocket.onerror = onSocketError;
        webSocket.onmessage = onSocketMessage;
        return webSocket;
    }

    /**
     * Is called when the WebSocket opens.
     */
    function onSocketOpen() {
        log(onSocketOpen.name);

        waitingForGraph = true;
        getGraph();
    }

    /**
     * Is called when the WebSocket closes.
     */
    function onSocketClose() {
        log(onSocketClose.name);
    }

    /**
     * Is called when the WebSocket throws an error.
     */
    function onSocketError() {
        log(onSocketError.name);
    }

    /**
     * Is called when the WebSocket retrieves a message.
     *
     * @param {Object} wsMessage - The message.
     */
    function onSocketMessage(wsMessage) {
        log(onSocketMessage.name);

        var wsMessageData = JSON.parse(wsMessage.data);
        switch (wsMessageData.type) {

            case "getGraph":
                log(onSocketMessage.name, "getGraph");
                var message = {
                    type: "gotGraph",
                    data: graph.toJSON()
                };
                messageQueue.push(message);
                processMessageQueue();
                break;

            case "gotGraph":
                log(onSocketMessage.name, "gotGraph");
                if (waitingForGraph && wsMessageData.data.cells) {
                    waitingForGraph = false;
                    graph.fromJSON(wsMessageData.data, {remote: true});
                }
                break;

            default:
                updateGraph(wsMessageData);
                break;
        }

    }

    /**
     * Is called when an event on the graph occurs.
     *
     * @param {string} eventName - The name of the event.
     * @param {joint.dia.Cell} cell - The cell on which the event occurred.
     * @param {Object} options - Options that were set when triggering the event.
     */
    function onGraphEvent(eventName, cell, options) {
        log(onGraphEvent.name, eventName);

        if (options && options.remote) {
            return;
        }

        if (config.realTime) {
            notifyCellChanged(eventName, cell);
        } else {
            processEventNoRealTime(eventName, cell);
        }
    }

    /**
     * Create the message for a graph event and send it.
     *
     * @param {string} eventName - The name of the event.
     * @param {joint.dia.Cell} cell - The cell on which the event occurred.
     */
    function notifyCellChanged(eventName, cell) {
        log(notifyCellChanged.name, eventName);

        if (isRemoteRelevant(eventName)) {
            var message = {
                type: "cellChanged",
                data: {
                    eventName: eventName,
                    cell: cell
                }
            };

            messageQueue.push(message);
            processMessageQueue();
        }
    }

    /**
     * Process an incoming graph event when the configuration realTime is false.
     *
     * @param {string} eventName - The name of the event.
     * @param {joint.dia.Cell} cell - The cell on which the event occurred.
     */
    function processEventNoRealTime(eventName, cell) {
        log(processEventNoRealTime.name, eventName);

        if (!isBatchEvent(eventName)) {
            notifyCellChanged(eventName, cell);
            return;
        }

        switch (eventName) {

            case "batch:start":
                batchStarted = true;
                break;

            case "batch:stop":
                if (batchEvent && batchCells.length) {
                    batchCells.forEach(function (cell) {
                        notifyCellChanged(batchEvent, cell);
                    });
                    batchEvent = null;
                    batchCells.length = 0;
                }
                batchStarted = false;
                break;

            default:
                if (isRemoteRelevant(eventName)) {
                    if (batchStarted) {
                        batchEvent = eventName;
                        if (batchCells.indexOf(cell) === -1) {
                            batchCells.push(cell);
                        }
                    } else {
                        notifyCellChanged(eventName, cell);
                    }
                }
                break;

        }
    }

    /**
     * Update the graph considering a remote message from the WebSocket.
     *
     * @param {Object} wsMessageData - The data-part of the WebSocket message.
     */
    function updateGraph(wsMessageData) {
        log(updateGraph.name);

        if (wsMessageData.error) {
            return;
        }

        var remoteCell = wsMessageData.data.cell;
        var localCell = graph.getCell(remoteCell.id);

        switch (wsMessageData.type) {
            case "cellChanged":

                switch (wsMessageData.data.eventName) {
                    case "add":
                        graph.addCell(remoteCell, {remote: true});
                        break;

                    case "remove":
                        if (localCell) {
                            localCell.remove({
                                disconnectLinks: true,
                                remote: true
                            });
                        }
                        break;

                    default:
                        if (localCell) {
                            var attribute = wsMessageData.data.eventName.substr("change:".length);
                            localCell.set(attribute, wsMessageData.data.cell[attribute], {remote: true});
                        }
                        break;
                }

                break;
        }
    }

    /**
     * Create the message for getting the graph from remote and send it.
     */
    function getGraph() {
        log(getGraph.name);

        var message = {
            type: "getGraph"
        };

        messageQueue.push(message);
        processMessageQueue();
    }

    /**
     * Send all messages from the messageQueue.
     */
    function processMessageQueue() {
        log(processMessageQueue.name);

        if (webSocket.readyState !== webSocket.OPEN) {
            return;
        }

        var notSent = [];

        messageQueue.forEach(function (message) {
            if (!sendMessage(message)) {
                notSent.push(message);
            }
        });

        messageQueue.length = 0;

        notSent.forEach(function (message) {
            messageQueue.push(message);
        });

        if (messageQueue.length) {
            processMessageQueue();
        }
    }

    /**
     * Send one message.
     *
     * @param {Object} message - The message to send.
     * @returns {boolean} - True if the message was successfully sent, otherwise false.
     */
    function sendMessage(message) {
        log(sendMessage.name);

        if (webSocket.readyState !== webSocket.OPEN) {
            return false;
        }

        webSocket.send(JSON.stringify(message));
        return true;
    }

    /**
     * Check whether an event will occur surrounded by batch:start and batch:stop events.
     *
     * @param {string} eventName - The event to check.
     * @returns {boolean} - True if the event is a batch event, otherwise false.
     */
    function isBatchEvent(eventName) {
        log(isBatchEvent.name, eventName);
        return config.nonBatchEvents.indexOf(eventName) === -1;
    }

    /**
     * Check whether an event is relevant for sending it to the WebSocket.
     *
     * @param {string} eventName - The event to check.
     * @returns {boolean} - True if the event is relevant, otherwise false.
     */
    function isRemoteRelevant(eventName) {
        log(isRemoteRelevant.name, eventName);
        return eventName === "add" || eventName === "remove" || eventName.substr(0, "change:".length) === "change:";
    }

    /**
     * Log a message to the browser console.
     * @param {string} fnName - The name of the function that wants to log something.
     * @param {string} [message] - The message to log.
     * @param {boolean} [force] - Force the logging, even if the debug configuration is set to false.
     */
    function log(fnName, message, force) {
        if (config.debug || force) {
            message = message || "";
            console.log("[" + fnName + "] " + message);
        }
    }

    return {
        init: init
    };
})();