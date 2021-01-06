import $ from "jquery";
import './online-socket.css'

export class OnlineSocket {

    constructor(area) {
        this.area = area;
        this.connect();
    }

    connect() {
        const service = this;
        try {
            this.webSocket = new WebSocket("ws://localhost:9000/socket");
            this.webSocket.onopen = function (openEvent) {
                console.log("WebSocket OPEN: " + JSON.stringify(openEvent, null, 4));
                service.pingOnlineStatus();
                setInterval(function () {
                    service.pingOnlineStatus();
                }, 30000);
            };
            this.webSocket.onclose = function (closeEvent) {
                console.log("WebSocket CLOSE: " + JSON.stringify(closeEvent, null, 4));
                setTimeout(function () {
                    service.connect();
                }, 3000);
            };
            this.webSocket.onerror = function (errorEvent) {
                console.log("WebSocket ERROR: " + JSON.stringify(errorEvent, null, 4));
            };
            this.webSocket.onmessage = function (messageEvent) {
                const msg = JSON.parse(messageEvent.data);
                if (msg.name === "onlineStatus")
                    OnlineSocket.renderOnlineUsers(msg.onlineClients);
            };
        } catch (exception) {
            console.error(exception);
            setTimeout(function () {
                service.connect();
            }, 3000);
        }
    }

    pingOnlineStatus() {
        const msg = JSON.stringify({
            name: "online",
            value: this.area
        });
        this.webSocket.send(msg);
    }

    static renderOnlineUsers(users) {
        const onlineUserElement = $('#online-users');
        onlineUserElement.empty();
        for (let i = 0; i < users.length; i++) {
            let el = $("<span>").addClass("online-user")
                .html(users[i].fullName);
            onlineUserElement.append(el);
        }
    }

}