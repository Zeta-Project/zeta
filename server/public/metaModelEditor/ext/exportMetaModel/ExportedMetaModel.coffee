###
  An instance of this class will be returned from the exporting process.
  It contains the information about the exported meta model.
###
class window.ExportedMetaModel

  constructor: () ->
    @valid = false
    @messages = null
    @metaModel = null
    @metaModelString = null


  setValid: (valid) ->
    @valid = valid


  setMessages: (messages) ->
    @messages = messages


  setMetaModel: (metaModel) ->
    @metaModel = metaModel


  isValid: () ->
    @valid


  getMessages: () ->
    @messages


  getMetaModel: () ->
    @metaModel


  toString: (prettify) ->
    if prettify
      JSON.stringify @metaModel, null, 2
    else
      JSON.stringify @metaModel