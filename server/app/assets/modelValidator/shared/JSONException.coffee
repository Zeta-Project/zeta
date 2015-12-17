class window.JSONException extends Error

  constructor: (which, message) ->
    @name = "JSONException"
    @message = "JSON of #{which} is corrupted"
    if message?
      @message += ": #{message}"