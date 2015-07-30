root = exports ? this

root.dataVisEditor = null

class DataVisEditor
  ace: root.dataVisAce
  parser: root.dataVisMode.getTokenizer()
  errors: []
  context: null
  scope: []

  load: (context) ->
    code = context.get("datavis")
    this.ace.setValue(code) if code?
    this.context = context
    root.dataVisClient.queryScope(context)

  unload: () ->
    this.ace.setValue("")
    this.context = null

  save: () ->
    this.errors = []
    code = this.ace.getValue()
    lines = this.parser.parse(code)
    this.errors.push token for token in line when token.error? for line in lines
    this.validateStyleAttributes(code)
    if this.errors.length > 0
      this.displayErrors()
    else
      this.hideErrors()
      this.context.set("datavis", code)
      root.dataVisClient.sendCode(this.context, code)

  validateStyleAttributes: (code) ->
    availableKeys = Object.keys(this.context.attributes.attrs)
    selectorsFromCode =  code.match(/'.*?'/g)
    selectors = []
    selectors.push selector.substr(1, selector.length - 2) for selector in selectorsFromCode if selectorsFromCode?
    valid = true
    for selector in selectors
      if root.jQuery.inArray(selector, availableKeys) == -1
        valid = false
        this.errors.push("Invalid style attribute:"+selector)
    valid

  displayErrors: () ->
    window.nodeCodeEditor.displayErrors(this.errors)

  hideErrors: () ->
    window.nodeCodeEditor.hideErrors()

  getMAttributeScope: () ->
    this.scope

  getStyleScope: () ->
    this.context.get("attrs")

  getClassName: () ->
    this.context.get("ecoreName")

root.DataVisEditor = () ->
  new DataVisEditor()

mAttributeScope =
  getCompletions: (editor, session, pos, prefix, callback) =>
    parserState = session.bgTokenizer.states[session.bgTokenizer.currentLine].parserState if session.bgTokenizer.states[session.bgTokenizer.currentLine]?
    if(parserState == "conditionOperandL" || parserState == "conditionOperandR" || prefix.length > 0 && (parserState == "comparator" || parserState=="delimiter"))
      callback(null, root.dataVisEditor.getMAttributeScope().map(mAttributeScope.mapMAttrs))

  mapMAttrs: (attr) ->
    name: attr,
    value: attr,
    score: 0,
    meta: "attribute"

styleAttributeScope =
  getCompletions: (editor, session, pos, prefix, callback) =>
    if(session.bgTokenizer.states[session.bgTokenizer.currentLine]? and session.bgTokenizer.states[session.bgTokenizer.currentLine].inBlock == "selectorString")
      styleAttributes = root.dataVisEditor.getStyleScope()
      delete styleAttributes["."]
      delete styleAttributes[".bounding-box"]
      delete styleAttributes["rect.bounding-box"]
      callback(null, Object.keys(styleAttributes).map(styleAttributeScope.mapStyleAttrs))

  mapStyleAttrs: (attr) ->
    name: attr,
    value: attr,
    score:0,
    meta: "style"

svgScope =
  scope: [
    "fill"
    "fill-opacity"
    "stroke"
    "stroke-width"
    "stroke-dasharray"
    "x"
    "y"
    "height"
    "width"
    "font-size"
    "font-family"
    "font-weight"
    "cx"
    "cy"
    "rx"
    "ry"
  ]

  getCompletions: (editor, session, pos, prefix, callback) =>
    parserState = session.bgTokenizer.states[session.bgTokenizer.currentLine].parserState if session.bgTokenizer.states[session.bgTokenizer.currentLine]?
    lex = session.bgTokenizer.states[session.bgTokenizer.currentLine].t if(session.bgTokenizer.states[session.bgTokenizer.currentLine]?)
    if(lex? && parserState? && (parserState == "assignmentTarget" || parserState == "assignmentOp") && (lex == "property" || lex == "."))
      callback(null, svgScope.scope.map(svgScope.mapSvgAttrs))

  mapSvgAttrs: (attr) ->
    name:attr
    value:attr
    score:0
    meta: "style"


langTools = root.ace.require("ace/ext/language_tools")
langTools.addCompleter(mAttributeScope)
langTools.addCompleter(styleAttributeScope)
langTools.addCompleter(svgScope)