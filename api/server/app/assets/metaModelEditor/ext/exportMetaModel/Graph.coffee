###
  Wrapper class around the internal JointJS graph.
  It provides methods for checking and extracting attributes from the graph.
###
class window.Graph

  constructor: (@graph) ->


# Returns all elements (which are real elements by MoDiGen metamodel semantics).
  getElements: () ->
    @graph.getElements()
    .filter (element) ->
      mCoreUtil.isElement element


# Returns the names of all elements.
  getElementNames: () ->
    @getElements()
    .map (element) ->
      element.attributes.name


# Returns all references (which are real references by MoDiGen metamodel semantics).
  getReferences: () ->
    @graph.getLinks()
    .filter (link) ->
      mCoreUtil.isReference link


# Returns the names of all references.
  getReferenceNames: () ->
    @getReferences()
    .map (reference) ->
      reference.attributes.name



# Returns the name of the given cell.
  getName: (cell) ->
    cell.attributes.name

# Returns the name of the given cell.
  getDescription: (cell) ->
    result = cell.attributes.description

    if result == "" or typeof(result) isnt 'string'
      ""



# Returns all element-, reference and enum-names which are assigned more than once.
  getDuplicateKeys: () ->
    keys = []
    duplicateKeys = []

    # elements
    for key in @getElementNames().concat(@getReferenceNames()).concat(mEnum.getMEnumNames())
      if keys.indexOf(key) == -1
        keys.push key
      else
        duplicateKeys.push key

    duplicateKeys


# Returns all attribute keys which are assigned more than once insinde an element.
  getDuplicateAttributes: () ->
    duplicateAttributes = []

    for cell in @getElements().concat @getReferences()
      if cell.attributes[Constants.field.ATTRIBUTES]?
        attributes = []
        for attribute in cell.attributes[Constants.field.ATTRIBUTES]
          key = attribute.name
          if attributes.indexOf(key) == -1
            attributes.push key
          else
            duplicateAttributes.push new Attribute cell.attributes.name, key

    duplicateAttributes


# Checks whether the element is abstract.
  isAbstract: (element) ->
    mCoreUtil.isAbstract element


# Returns all superTypes of the given element (which is defined by the generalization reference type).
  getSuperTypes: (element) ->
    @graph.getConnectedLinks element, {outbound: true}
    .filter (link) ->
      mCoreUtil.isGeneralization link
    .map ((link) ->
        @graph.getCell(link.attributes.target.id).attributes.name),
      @


# Returns the attributes of the cell.
# Default value is parsed from String because the inspector can't display the correct input
# based on the selected type, because the type is in a List
# See: http://stackoverflow.com/questions/37742721/display-field-based-on-another-in-jointjs
  getAttributes: (cell) ->
    mAttributes = []
    if cell.attributes[Constants.field.ATTRIBUTES]?
      for attributes in cell.attributes[Constants.field.ATTRIBUTES]
        mAttributes.push({});
        for key, value of attributes
          if key == 'default'
            switch attributes.type
              when 'Bool'
                mAttributes[mAttributes.length - 1][key] = value == 'true'
              when 'Int'
                if !this.isNumeric(value)
                  value = 0
                mAttributes[mAttributes.length - 1][key] = parseInt(value)
              when 'Double'
                if !this.isNumeric(value)
                  value = 0.0
                mAttributes[mAttributes.length - 1][key] = parseFloat(value)
              else mAttributes[mAttributes.length - 1][key] = value
          else mAttributes[mAttributes.length - 1][key] = value
    mAttributes

# Returns the methodss of the cell.
  getEntityMethods: (cell) ->
    mMethods = []
    if cell.attributes[Constants.field.METHODS]?
      for attributes in cell.attributes[Constants.field.METHODS]
        mMethods.push({});
        for key, value of attributes
          mMethods[mMethods.length - 1][key] = value
    mMethods

  isNumeric: (n) ->
    !isNaN(parseFloat(n)) && isFinite(n);

# Returns all input references of the element.
  getInputs: (element) ->
    inputs = @graph.getConnectedLinks element, {inbound: true}
    .filter (link) ->
      mCoreUtil.isReference link
    @createLinkdef element, inputs, Constants.field.LINKDEF_INPUT


# Returns all output references of the element.
  getOutputs: (element) ->
    outputs = @graph.getConnectedLinks element, {outbound: true}
    .filter (link) ->
      mCoreUtil.isReference link
    @createLinkdef element, outputs, Constants.field.LINKDEF_OUTPUT


# Returns all source classes of the reference.
  getSources: (reference) ->
    sources = [@graph.getCell reference.attributes.source.id]
    @createLinkdef reference, sources, Constants.field.LINKDEF_SOURCE


# Returns all target classes of the reference.
  getTargets: (reference) ->
    targets = [@graph.getCell reference.attributes.target.id]
    @createLinkdef reference, targets, Constants.field.LINKDEF_TARGET


# Returns the sourceDeletionDeletesTarget value of the reference.
  getSourceDeletionDeletesTarget: (reference) ->
    reference.attributes[Constants.field.SOURCE_DELETION_DELETES_TARGET] || false



# Returns the targetDeletionDeletesSource value of the reference.
  getTargetDeletionDeletesSource: (reference) ->
    reference.attributes[Constants.field.TARGET_DELETION_DELETES_SOURCE] || false


  ###
    Creates the linkdef object for the element.
    connectedCells is an array of the directly connected cells (if element is a class, connectedCells is a list
    of references, if element is a reference, connectedCells is a list of classes).
    FieldName is inputs, outputs, source or target.
  ###
  createLinkdef: (element, connectedCells, fieldName) ->
    linkdef = []

    for cell in connectedCells
      obj =
        type: cell.attributes.name
        upperBound: 1
        lowerBound: 1
        deleteIfLower: false

      if element.attributes? && element.attributes[fieldName]?
        match = element.attributes[fieldName]
        .filter (field) ->
          field.type == obj.type

        if match.length > 0
          obj =
            type: obj.type
            upperBound: match[0].upperBound
            lowerBound: match[0].lowerBound
            deleteIfLower: match[0].deleteIfLower

      linkdef.push obj

    linkdef



