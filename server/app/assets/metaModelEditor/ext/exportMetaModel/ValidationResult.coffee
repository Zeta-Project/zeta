class window.ValidationResult

  constructor: () ->
    @valid = true
    @messages = []

  addErrorMessage: (message) ->
    @valid = false
    @messages.push message

  isValid: () ->
    @valid

  getMessages: () ->
    @messages