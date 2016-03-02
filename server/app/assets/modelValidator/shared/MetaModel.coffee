class window.MetaModel

  Bounds = window.Bounds
  Attribute = window.Attribute
  Constants = window.Constants


  constructor: (@metaModel) ->


  get: (key) ->
    if @has key then @metaModel.find (element) -> element.name == key else null


  has: (key) ->
    (@metaModel.find (element) ->
      element.name == key)?


  hasClass: (key) ->
    @has(key) && @get(key)[Constants.M_TYPE] == Constants.M_CLASS


  hasReference: (key) ->
    @has(key) && @get(key)[Constants.M_TYPE] == Constants.M_REFERENCE


  getInputBounds: (classKey, referenceKey) ->
    @getBounds classKey, referenceKey, Constants.INPUTS


  getOutputBounds: (classKey, referenceKey) ->
    @getBounds classKey, referenceKey, Constants.OUTPUTS


  getSourceBounds: (referenceKey, classKey) ->
    @getBounds referenceKey, classKey, Constants.SOURCES


  getTargetBounds: (referenceKey, classKey) ->
    @getBounds referenceKey, classKey, Constants.TARGETS


  getBounds: (key, type, field) ->
    if !(@has(key) && @has(type))
      return null

    fieldArray = @get(key)[field]

    boundsObjects = fieldArray.filter (field) ->
      field[Constants.TYPE] == type

    if boundsObjects.length > 0
      return new Bounds boundsObjects[0][Constants.attr.LOWER_BOUND], boundsObjects[0][Constants.attr.UPPER_BOUND]

    inheritedBounds = []

    if @hasSuperTypes key
      for superType in @getSuperTypes key
        bounds = @getBounds superType, type, field
        if bounds?
          inheritedBounds.push bounds

    if @hasSuperTypes type
      for superType in @getSuperTypes type
        bounds = @getBounds key, superType, field
        if bounds?
          inheritedBounds.push bounds

    if inheritedBounds.length == 1
      return inheritedBounds[0]

    null


  hasAttributes: (key) ->
    @has(key) && @get(key)[Constants.ATTRIBUTES]?


  hasSuperTypes: (key) ->
    @has(key) && @get(key)[Constants.SUPER_TYPES]?


  getSuperTypes: (key) ->
    if @hasSuperTypes key then @get(key)[Constants.SUPER_TYPES] else null


  getDirectAttributes: (key) ->
    if @has(key) && @hasAttributes(key) then @get(key)[Constants.ATTRIBUTES] else null


  getAttribute: (objectKey, attributeKey) ->
    if @hasAttributes(objectKey) && (@getDirectAttributes(objectKey).find (attribute) -> attribute.name == attributeKey)?
      return new Attribute @getDirectAttributes(objectKey).find (attribute) -> attribute.name == attributeKey

    inheritedAttributes = []
    if @hasSuperTypes objectKey
      for superType in @getSuperTypes objectKey
        attribute = @getAttribute superType, attributeKey
        if attribute?
          inheritedAttributes.push attribute

    if inheritedAttributes.length == 1
      return inheritedAttributes[0]

    null
