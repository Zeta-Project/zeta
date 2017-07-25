###
  An instance of this class will be returned from the exporting process.
  It contains the information about the exported meta model.
###
class window.ExportedMetaModel

  constructor: () ->
    @valid = false
    @messages = null
    # @metaModel = null
    @classes = null
    @references = null
    @enums = null
    @attributes = null
    @methods = null
    # @metaModelString = null


  setValid: (valid) ->
    @valid = valid

  isValid: () ->
    @valid


  setMessages: (messages) ->
    @messages = messages

  getMessages: () ->
    @messages

  # setMetaModel: (metaModel) ->
  #  @metaModel = metaModel
    # @metaModelString = @toString()

  # getMetaModel: () ->
   # @metaModel


  setClasses: (classes) ->
    @classes = classes

  getClasses: () ->
    @classes


  setReferences: (references) ->
    @references = references

  getReferences: () ->
    @references


  setEnums: (enums) ->
    @enums = enums

  getEnums: () ->
    @enums


  setAttributes: (attributes) ->
    @attributes = attributes

  getAttributes: () ->
    @attributes


  setMethods: (methods) ->
    @methods = methods

  getMethods: () ->
    @methods


  # toString: (prettify) ->
  #  if prettify
  #    JSON.stringify @metaModel, null, 2
  #  else
  #    JSON.stringify @metaModel