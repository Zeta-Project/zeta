class window.ModelValidator

  MetaModel = window.MetaModel
  Instance = window.Instance
  ValidationResult = window.ValidationResult
  JSONException = window.JSONException
  Bounds = window.Bounds


  constructor: (metaModel) ->
    if metaModel?
      @setMetaModel metaModel


  setMetaModel: (metaModel) ->
    metaModelJSON = null

    if typeof metaModel == 'object'
      metaModelJSON = metaModel

    else
      try
        metaModelJSON = JSON.parse metaModel
      catch e
        throw new JSONException "meta model", e.message

    @metaModel = new MetaModel metaModelJSON


  validate: (instance) ->
    if !@metaModel?
      throw "meta model is not initialized"

    instanceJSON = null

    if typeof instance == 'object'
      instanceJSON = instance

    else
      try
        instanceJSON = JSON.parse instance
      catch e
        throw new JSONException "instance", e.message

    @instance = new Instance instanceJSON
    @result = new ValidationResult
    @globalUniqueAttributes = {}

    for instanceKey in @instance.getKeys()
      @checkMObject instanceKey

    @result


  checkMObject: (instanceKey) ->
    if @instance.isClass instanceKey
      @checkMClass instanceKey
    else if @instance.isReference instanceKey
      @checkMReference instanceKey
    else
      @result.addErrorMessage "'#{instanceKey}' is neither mClass nor mReference."


  checkMClass: (instanceKey) ->
    className = @instance.getClassName instanceKey
    if !className?
      @result.addErrorMessage "Class '#{instanceKey}' is broken."
      return

    if !@metaModel.hasClass className
      @result.addErrorMessage "Class '#{className}' does not exist in meta model."
      return

    @checkInputs instanceKey, className
    @checkOutputs instanceKey, className
    @checkAttributes instanceKey


  checkMReference: (instanceKey) ->
    referenceName = @instance.getReferenceName instanceKey
    if !referenceName?
      @result.addErrorMessage "Reference '#{instanceKey}' is broken."
      return

    if !@metaModel.hasReference referenceName
      @result.addErrorMessage "Reference '#{referenceName}' does not exist in meta model."
      return

    @checkSources instanceKey, referenceName
    @checkTargets instanceKey, referenceName
    @checkAttributes instanceKey


  checkInputs: (instanceKey, className) ->
    inputs = @instance.getInputs instanceKey
    if !inputs?
      @result.addErrorMessage "Class '#{instanceKey}' does not have a 'inputs' field."
      return

    for referenceKey, values of inputs
      bounds = @metaModel.getInputBounds className, referenceKey

      if !bounds?
        @result.addErrorMessage "Input reference '#{referenceKey}' is not allowed for class '#{className}'."
        return

      switch bounds.compareTo values.length
        when Bounds.TOO_LOW
          @result.addErrorMessage "Input count #{values.length} of '#{instanceKey}' for '#{referenceKey}' is lower than lower bound #{bounds.getLowerBound()} in '#{className}'."
        when Bounds.TOO_HIGH
          @result.addErrorMessage "Input count #{values.length} of '#{instanceKey}' for '#{referenceKey}' is higher than upper bound #{bounds.getUpperBound()} in '#{className}'."


  checkOutputs: (instanceKey, className) ->
    outputs = @instance.getOutputs instanceKey
    if !outputs?
      @result.addErrorMessage "Class '#{instanceKey}' does not have a 'outputs' field."
      return

    for referenceKey, values of outputs
      bounds = @metaModel.getOutputBounds className, referenceKey

      if !bounds?
        @result.addErrorMessage "Output reference '#{referenceKey}' is not allowed for class '#{className}'."
        return

      switch bounds.compareTo values.length
        when Bounds.TOO_LOW
          @result.addErrorMessage "Output count #{values.length} of '#{instanceKey}' for '#{referenceKey}' is lower than lower bound #{bounds.getLowerBound()} in '#{className}'."
        when Bounds.TOO_HIGH
          @result.addErrorMessage "Output count #{values.length} of '#{instanceKey}' for '#{referenceKey}' is higher than upper bound #{bounds.getUpperBound()} in '#{className}'."


  checkSources: (instanceKey, referenceName) ->
    sources = @instance.getSources instanceKey
    if !sources?
      @result.addErrorMessage "Reference '#{instanceKey}' does not have a 'source' field."
      return

    for classKey, values of sources
      bounds = @metaModel.getSourceBounds referenceName, classKey

      if !bounds?
        @result.addErrorMessage "Source class '#{classKey}' is not allowed for reference '#{referenceName}'."
        return

      switch bounds.compareTo values.length
        when Bounds.TOO_LOW
          @result.addErrorMessage "Source count #{values.length} of '#{instanceKey}' for '#{classKey}' is lower than lower bound #{bounds.getLowerBound()} in '#{referenceName}'."
        when Bounds.TOO_HIGH
          @result.addErrorMessage "Source count #{values.length} of '#{instanceKey}' for '#{classKey}' is higher than upper bound #{bounds.getUpperBound()} in '#{referenceName}'."


  checkTargets: (instanceKey, referenceName) ->
    targets = @instance.getTargets instanceKey
    if !targets?
      @result.addErrorMessage "Reference '#{instanceKey}' does not have a 'target' field."
      return

    for classKey, values of targets
      bounds = @metaModel.getTargetBounds referenceName, classKey

      if !bounds?
        @result.addErrorMessage "Target class '#{classKey}' is not allowed for reference #{referenceName}."
        return

      switch bounds.compareTo values.length
        when Bounds.TOO_LOW
          @result.addErrorMessage "Target count #{values.length} of '#{instanceKey}' for '#{classKey}' is lower than lower bound '#{bounds.getLowerBound()}' in '#{referenceName}'."
        when Bounds.TOO_HIGH
          @result.addErrorMessage "Target count #{values.length} of '#{instanceKey}' for '#{classKey}' is higher than upper bound #{bounds.getUpperBound()} in '#{referenceName}'."


  checkAttributes: (instanceKey) ->
    if !@instance.hasAttributes instanceKey
      return

    attributeKeys = @instance.getAttributeKeys instanceKey
    if !attributeKeys?
      return

    for attributeKey in attributeKeys
      mObjName = null
      if @instance.isClass instanceKey
        mObjName = @instance.getClassName instanceKey
      else
        mObjName = @instance.getReferenceName instanceKey

      if !mObjName?
        @result.addErrorMessage "'#{instanceKey}' is broken."
        continue

      metaAttribute = @metaModel.getAttribute mObjName, attributeKey
      if !metaAttribute?
        @result.addErrorMessage "Attribute '#{attributeKey}' does not exist in object '#{mObjName}'."
        continue

      instanceAttributeValues = @instance.getAttribute instanceKey, attributeKey
      if !instanceAttributeValues?
        @result.addErrorMessage "'#{mObjName}' '#{instanceKey}' has no attributes with key '#{attributeKey}'."
        continue

      attributeCount = instanceAttributeValues.length
      switch metaAttribute.compareTo attributeCount
        when Bounds.TOO_LOW
          @result.addErrorMessage "'#{mObjName}' '#{instanceKey}' has #{attributeCount} attributes of type '#{attributeKey}', lower bound is #{metaAttribute.getUpperBound()}."
        when Bounds.TOO_HIGH
          @result.addErrorMessage "'#{mObjName}' '#{instanceKey}' has #{attributeCount} attributes of type '#{attributeKey}', upper bound is #{metaAttribute.getLowerBound()}."

      if metaAttribute.isUniqueGlobal()
        @checkUniqueGlobalAttribute attributeKey, instanceAttributeValues

      if metaAttribute.isUniqueLocal()
        @checkUniqueLocalAttribute attributeKey, instanceAttributeValues


  checkUniqueGlobalAttribute: (key, values) ->
    for value in values
      if @globalUniqueAttributes[key]?
        if @globalUniqueAttributes[key].indexOf(value) == -1
          @globalUniqueAttributes[key].push value
        else
          @result.addErrorMessage "Attribute '#{key}' is not globally unique (duplicate value '#{value}')."
      else
        @globalUniqueAttributes[key] = [value]


  checkUniqueLocalAttribute: (key, values) ->
    duplicateValues = []
    for value in values
      if values.indexOf(value) != values.lastIndexOf(value) && duplicateValues.indexOf(value) == -1
        duplicateValues.push value
        @result.addErrorMessage "Attribute '#{key}' is not locally unique (duplicate value '#{value}')."

