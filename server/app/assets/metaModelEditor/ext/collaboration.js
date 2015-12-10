var collaboration = (function () {
    'use strict';

    var debug = false;
    var realTime = true;
    var webSocketUri = "ws://" + window.location.host + "/metaModelSocket/" + window.loadedMetaModel.uuid;
    var userUuid = window.loadedMetaModel.userUuid;
    var nonBatchEvents = ["add", "remove", "change:source", "change:target", "change:attrs", "change:embeds", "change:parent", "change:z", "change:smooth", "change:manhattan"];

    var graph;
    var paper;
    var socket;

    var messageQueue = [];

    var waitingForGraph = false;

    var batchStarted = false;
    var batchEvent = null;
    var batchCells = [];

    function init(_graph, _paper) {
        log("init");

        graph = _graph;
        paper = _paper;
        socket = createSocket();
        graph.on("all", onGraphEvent);
    }

    function createSocket() {
        log("createSocket");

        var socket = new WebSocket(webSocketUri);
        socket.onopen = onSocketOpen;
        socket.onclose = onSocketClose;
        socket.onerror = onSocketError;
        socket.onmessage = onSocketMessage;
        return socket;
    }

    function onSocketOpen() {
        log("onSocketOpen");
        waitingForGraph = true;
        getGraph();
    }

    function onSocketClose() {
        log("onSocketClose");
    }

    function onSocketError() {
        log("onSocketError");
    }

    function onSocketMessage(wsMessage) {
        log("onSocketMessage");

        var wsMessageData = JSON.parse(wsMessage.data);
        switch (wsMessageData.type) {

            case "getGraph":
                log("onSocketMessage", "getGraph");
                var message = {
                    type: "gotGraph",
                    userUuid: userUuid,
                    data: graph.toJSON()
                };
                messageQueue.push(message);
                send();
                break;

            case "gotGraph":
                log("onSocketMessage", "gotGraph");
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

    function onGraphEvent(eventName, cell, data, options) {
        log("onGraphEvent", eventName);

        if (options && options.remote) {
            return;
        }

        if (realTime) {
            notifyCellChanged(eventName, cell);
        } else {
            processEventNoRealTime(eventName, cell);
        }
    }

    function notifyCellChanged(eventName, cell) {
        log("notifyCellChanged", eventName);
        if (isRemoteRelevant(eventName)) {
            var message = {
                type: "cellChanged",
                userUuid: userUuid,
                data: {
                    eventName: eventName,
                    cell: cell
                }
            };
            messageQueue.push(message);
            send();
        }
    }

    function processEventNoRealTime(eventName, cell) {
        log("processEventNoRealTime", eventName);

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

    function updateGraph(wsMessage) {
        log("updateGraph");

        if (wsMessage.error) {
            return;
        }

        var remoteCell = wsMessage.data.cell;
        var localCell = graph.getCell(remoteCell.id);

        switch (wsMessage.type) {
            case "cellChanged":

                switch (wsMessage.data.eventName) {
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
                            var attribute = wsMessage.data.eventName.substr("change:".length);
                            localCell.set(attribute, wsMessage.data.cell[attribute], {remote: true});
                        }
                        break;
                }

                break;
        }
    }

    function getGraph() {
        log("getGraph");

        var message = {
            type: "getGraph",
            userUuid: userUuid
        };

        messageQueue.push(message);
        send();
    }

    function send() {
        log("send");

        if (socket.readyState !== socket.OPEN) {
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
            send();
        }
    }

    function sendMessage(message) {
        log("sendMessage");

        if (socket.readyState !== socket.OPEN) {
            return false;
        }

        socket.send(JSON.stringify(message));
        return true;
    }

    function isBatchEvent(eventName) {
        return nonBatchEvents.indexOf(eventName) === -1;
    }

    function isRemoteRelevant(eventName) {
        log("isRemoteRelevant", eventName);
        return eventName === "add" || eventName === "remove" || eventName.substr(0, "change:".length) === "change:";
    }

    function log(fnName, message, force) {
        if (debug || force) {
            message = message || "";
            console.log("[" + fnName + "] " + message);
        }
    }

    return {
        init: init
    };
})();