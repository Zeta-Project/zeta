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
        methods: @graph.getClassMethods element
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
        methods: [] # TODO
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
    ### TODO
    for thisAttribute in @graph.getElements()
      attributes.push
        mType: Constants.CLASS
        name: @graph.getName element
        abstract: @graph.isAbstract element
        superTypes: @graph.getSuperTypes element
        attributes: @graph.getAttributes element
        inputs: @graph.getInputs element
        outputs: @graph.getOutputs element ###
    attributes


  createMethods: (metaModel) ->
    methods = []
    ### TODO
    for method in @graph.getElements()
      methods.push
        mType: Constants.METHODS
        name: @graph.getName element
        abstract: @graph.isAbstract element
        superTypes: @graph.getSuperTypes element
        attributes: @graph.getAttributes element
        inputs: @graph.getInputs element
        outputs: @graph.getOutputs element ###
    methods

