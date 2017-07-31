# Main class for exporting the meta model into its JSON serialization.
class window.Exporter

  ###
    Constructor function.
    graph: the internal representation of the JointJS graph.
  ###
  constructor: (graph) ->
    @graph = new Graph graph


  ###
    Exports the graph.
    This is the only public method of this class!
    returns an instance of the ExportedMetaModel class which contains the exported JSON graph and error messages.
  ###
  export: () ->
    exportedModel = new ExportedMetaModel

    validationResult = @checkValidity()
    exportedModel.setValid validationResult.isValid()
    exportedModel.setMessages validationResult.getMessages()

    if validationResult.isValid()
      exportedModel.setClasses @createClasses()
      exportedModel.setReferences @createReferences()
      exportedModel.setEnums @createEnums()
      exportedModel.setAttributes @createAttributes()
      exportedModel.setMethods @createMethods()

    exportedModel


  ###
    Checks, if the graph is in a state that allows exporting it.
    returns an instance of the ValidationResult class.
  ###
  checkValidity: () ->
    validationResult = new ValidationResult

    for key in @graph.getDuplicateKeys()
      validationResult.addErrorMessage "Duplicate key '#{key}'"

    for attribute in @graph.getDuplicateAttributes()
      validationResult.addErrorMessage "Duplicate attribute '#{attribute.getAttributeKey()}' in cell '#{attribute.getCellName()}'"

    validationResult


  createClasses: () ->
    classes = []
    for element in @graph.getElements()
      classes.push
        name: @graph.getName element
        description: @graph.getDescription element
        abstractness: @graph.isAbstract element
        superTypeNames: @graph.getSuperTypes element
        attributes: @graph.getAttributes element
        methods: @graph.getEntityMethods element
        inputs: @graph.getInputs element
        outputs: @graph.getOutputs element
    classes

  createReferences: () ->
    references = []
    for reference in @graph.getReferences()
      references.push
        name: @graph.getName reference
        description: "" #TODO @graph.getDescription reference
        sourceDeletionDeletesTarget: @graph.getSourceDeletionDeletesTarget reference
        targetDeletionDeletesSource: @graph.getTargetDeletionDeletesSource reference
        attributes: @graph.getAttributes reference
        methods: @graph.getEntityMethods reference
        source: @graph.getSources reference
        target: @graph.getTargets reference
    references

  createEnums: () ->
    enums = []
    #TODO: on a saved and re-opened instance, mEnums are not set
    for thisMEnum in mEnum.getMEnums()
      enums.push
        name: thisMEnum.name
        values: thisMEnum.symbols
    enums



  createAttributes: () ->
    attributes = []

    if (mEnum.getMEnumContainer().attributes.hasOwnProperty('m_attributes'))
      for thisAttribute in mEnum.getMEnumContainer().attributes['m_attributes']
        attributes.push
          name: thisAttribute.name
          upperBound: thisAttribute.upperBound
          lowerBound: thisAttribute.lowerBound
          default: thisAttribute.default
          typ: thisAttribute.typ
          expression: thisAttribute.expression
          localUnique: thisAttribute.localUnique
          globalUnique: thisAttribute.globalUnique
          constant: thisAttribute.constant
          ordered: thisAttribute.ordered
          transient: thisAttribute.transient
          singleAssignment: thisAttribute.singleAssignment

    attributes


  createMethods: () ->
    methods = []

    if (mEnum.getMEnumContainer().attributes.hasOwnProperty('m_methods'))
      for thisMethod in mEnum.getMEnumContainer().attributes['m_methods']
        methods.push
          name: thisMethod.name
          parameters: thisMethod.parameters
          description: thisMethod.description
          returnType: thisMethod.returnType
          code: thisMethod.code
    methods
