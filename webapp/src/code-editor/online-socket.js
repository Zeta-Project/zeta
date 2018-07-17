export class OnlineSocket {

    constructor(area) {
        const service = this;
        this.area = area;
        try {
            this.webSocket = new WebSocket("ws://localhost:9000/socket");
            this.webSocket.onopen = function (openEvent) {
                console.log("WebSocket OPEN: " + JSON.stringify(openEvent, null, 4));
                service.pingOnlineStatus();
            };
            this.webSocket.onclose = function (closeEvent) {
                console.log("WebSocket CLOSE: " + JSON.stringify(closeEvent, null, 4));
                service.pingOfflineStatus();
            };
            this.webSocket.onerror = function (errorEvent) {
                console.log("WebSocket ERROR: " + JSON.stringify(errorEvent, null, 4));
            };
            this.webSocket.onmessage = function (messageEvent) {
                const wsMsg = messageEvent.data;
                console.log("WebSocket MESSAGE: " + wsMsg);
            };
        } catch (exception) {
            console.error(exception);
        }
    }

    pingOnlineStatus() {
        const msg = JSON.stringify({
            name: "online",
            value: this.area
        });
        this.webSocket.send(msg);
    }

    pingOfflineStatus() {
        const msg = JSON.stringify({
            name: "offline",
            value: this.area
        });
        this.webSocket.send(msg);
    }

}