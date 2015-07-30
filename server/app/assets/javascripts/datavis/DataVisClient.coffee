root = exports ? this

debug = true

class DataVisClient
  ####
  #Sent Messages
  ####

  sendCode: (context, code) ->
    objectId = context.id
    message =
      $type: "models.WebSocketMessage.DataVisCodeMessage"
      context: objectId
      classname: context.get("ecoreName")
      code: code
    root.webSocket.send(message)

  queryScope: (context) ->
    this.log("Querying scope for "+context.id);
    message =
      $type: "models.WebSocketMessage.DataVisScopeQuery"
      classname: context.get("ecoreName")
    root.webSocket.send(message)

  ####
  # Received Messages
  ####
  loadFile: (context, path) ->
    cell = window.globalGraph.getCell(context)
    if cell?
      root.jQuery.getScript(path)
    else
      this.log("Discarded script file for non-existing object.")

  scoping: (classname, scope) ->
    editor = root.dataVisEditor
    if editor? && editor.getClassName() == classname
      editor.scope = scope
    else
      this.log("Discarded response to outdated scoping query.")

  handleErrors: (context, errors) ->
    editor = root.dataVisEditor
    if editor? && editor.context.id == context
      editor.errors = errors
      editor.displayErrors()
    else
      this.log("Received Errors on closed or outdated editor session.")

  ####
  # Utilities
  ####
  log: (message) ->
    message ?= ""
    root.console.log("[DataVisClient]" + message) if debug

root.dataVisClient = new DataVisClient()
