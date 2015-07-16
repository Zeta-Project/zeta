class window.Attribute extends window.Bounds

  Constants = window.Constants


  constructor: (attributeObj) ->
    super attributeObj[Constants.attr.LOWER_BOUND], attributeObj[Constants.attr.UPPER_BOUND]
    @name = attributeObj[Constants.attr.NAME] || ''
    @uniqueGlobal = attributeObj[Constants.attr.UNIQUE_GLOBAL] || false
    @uniqueLocal = attributeObj[Constants.attr.UNIQUE_LOCAL] || false
    @default = attributeObj[Constants.attr.DEFAULT] || ''
    @singleAssignment = attributeObj[Constants.attr.SINGLE_ASSIGNMENT] || false
    @expression = attributeObj[Constants.attr.EXPRESSION] || ''
    @type = attributeObj[Constants.attr.TYPE] || ''
    @ordered = attributeObj[Constants.attr.ORDERED] || false
    @transient = attributeObj[Constants.attr.TRANSIENT] || false
    @constant = attributeObj[Constants.attr.CONSTANT] || false


  getName: () ->
    @name


  isUniqueGlobal: () ->
    @uniqueGlobal


  isUniqueLocal: () ->
    @uniqueLocal


  getDefault: () ->
    @default


  isSingleAssignment: () ->
    @singleAssignment


  getExpression: () ->
    @expression


  getType: () ->
    @type


  isOrdered: () ->
    @ordered


  isTransient: () ->
    @transient


  isConstant: () ->
    @constant