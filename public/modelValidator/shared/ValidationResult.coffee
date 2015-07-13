class window.ValidationResult

  constructor: () ->
    @valid = true
    @messages = []


  isValid: ->
    @valid


  getMessages: ->
    @messages


  addErrorMessage: (message) ->
    @valid = false
    @messages.push message