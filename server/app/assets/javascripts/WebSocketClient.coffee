root = exports ? this

debug = true
WEB_SOCKET_URI = "ws://" + window.location.host + "/modelSocket/" + window._global_graph_type + "/" + window._global_uuid

class WebSocketClient
  socket: null
  messageQueue: []

  create: () ->
    this.log("Create socket")
    this.log(WEB_SOCKET_URI)
    this.socket = new WebSocket(WEB_SOCKET_URI)
    this.socket.onmessage = this.onMessage

  onMessage: (messageEvent) =>
    this.log("message received")
    data = JSON.parse(messageEvent.data)
    this.log(data.$type)
    switch data.$type
      when "shared.DiagramWSOutMessage.NewScriptFile" then root.dataVisClient.loadFile(data.context, data.path)
      when "shared.DiagramWSOutMessage.DataVisScope" then root.dataVisClient.scoping(data.classname, data.scope)
      when "shared.DiagramWSOutMessage.DataVisError" then root.dataVisClient.handleErrors(data.context, data.msg)

  send: (messageObject) ->
    this.log("send")
    this.messageQueue.push(messageObject)
    this.sendQueue()

  sendQueue: () ->
    this.log("send queue")
    return if not this.isSocketOpen()
    notSent = []
    (notSent.push message if this.sendMessage(message) == false) for message in this.messageQueue
    this.messageQueue = []
    this.messageQueue.push message for message in notSent
    this.sendQueue if this.messageQueue.length > 0

  sendMessage: (message) ->
    this.log("sendMessage")
    return false if not this.isSocketOpen()
    this.socket.send(JSON.stringify(message))
    true

  isSocketOpen: () ->
    this.socket.readyState == this.socket.OPEN

  log: (message) ->
    message ?= ""
    root.console.log("[WebSocketClient] " + message) if debug

root.webSocket = new WebSocketClient()
root.webSocket.create()