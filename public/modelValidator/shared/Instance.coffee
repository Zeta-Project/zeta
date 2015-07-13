class window.Instance

  Constants = window.Constants


  constructor: (@instance) ->


  getKeys: () ->
    Object.keys @instance


  get: (key) ->
    if @has key
      @instance[key]
    else null


  has: (key) ->
    @instance[key]?


  isClass: (key) ->
    @has(key) && @get(key)[Constants.M_CLASS]?


  isReference: (key) ->
    @has(key) && @get(key)[Constants.M_REFERENCE]?


  getClassName: (key) ->
    if @isClass key
      @get(key)[Constants.M_CLASS]
    else null


  getReferenceName: (key) ->
    if @isReference(key)
      @get(key)[Constants.M_REFERENCE]
    else null


  getInputs: (key) ->
    if @has(key) && @get(key)[Constants.INPUTS]?
      @get(key)[Constants.INPUTS]
    else null


  getOutputs: (key) ->
    if @has(key) && @get(key)[Constants.OUTPUTS]?
      @get(key)[Constants.OUTPUTS]
    else null


  getSources: (key) ->
    if @has(key) && @get(key)[Constants.SOURCES]?
      @get(key)[Constants.SOURCES]
    else null


  getTargets: (key) ->
    if @has(key) && @get(key)[Constants.TARGETS]?
      @get(key)[Constants.TARGETS]
    else null


  hasAttributes: (key) ->
    @has(key) && @get(key)[Constants.ATTRIBUTES]?


  getAttributeKeys: (key) ->
    if @hasAttributes(key)
      Object.keys @get(key)[Constants.ATTRIBUTES]
    else null


  getAttribute: (key, attributeKey) ->
    if @hasAttributes(key) && @get(key)[Constants.ATTRIBUTES][attributeKey]?
      @get(key)[Constants.ATTRIBUTES][attributeKey]
    else null
