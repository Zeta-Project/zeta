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
      exportedModel.setMetaModel @createMetaModel()

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


  # Builds the metaModel JSON object and returns it.
  createMetaModel: () ->
    metaModel = []

    @addClasses metaModel
    @addReferences metaModel
    @addEnums metaModel

    metaModel


  # Iterates over the graph-elements and adds them to the metaModel object.
  addClasses: (metaModel) ->
    for element in @graph.getElements()

      metaModel.push
        mType: Constants.CLASS
        name: @graph.getName element
        description: "TODO" # @graph.getDescription is not working when empty
        abstract: @graph.isAbstract element
        superTypes: @graph.getSuperTypes element
        attributes: @graph.getAttributes element
        methods: @graph.getClassMethods element
        inputs: @graph.getInputs element
        outputs: @graph.getOutputs element



  # Iterates over the graph-references and adds them to the metaModel object.
  addReferences: (metaModel) ->
    for reference in @graph.getReferences()

      metaModel.push
        mType: Constants.REFERENCE
        name: @graph.getName reference
        description: "TODO" # @graph.getDescription is not working when empty
        sourceDeletionDeletesTarget: @graph.getSourceDeletionDeletesTarget reference
        targetDeletionDeletesSource: @graph.getTargetDeletionDeletesSource reference
        attributes: @graph.getAttributes reference
        methods: [] # TODO
        source: @graph.getSources reference
        target: @graph.getTargets reference


  # Iterates over the values of the mEnumContainer and adds them to the metaModel object.
  addEnums: (metaModel) ->
    #TODO: on a saved and re-opened instance, menums are not set
    for thisMEnum in mEnum.getMEnums()
      metaModel.push
        mType: Constants.ENUM
        name: thisMEnum.name
        symbols: thisMEnum.symbols

  ###
  # Iterates over the graph-elements and adds them to the metaModel object.
  addAttribute: (metaModel) ->
    mAttributes = []
    for thisAttribute in @graph.getElements()
      metaModel.push
        mType: Constants.CLASS
        name: @graph.getName element
        abstract: @graph.isAbstract element
        superTypes: @graph.getSuperTypes element
        attributes: @graph.getAttributes element
        inputs: @graph.getInputs element
        outputs: @graph.getOutputs element

    mAttributes

  # Iterates over the graph-elements and adds them to the metaModel object.
  addMethod: (metaModel) ->
      metaModel.push
        mType: Constants.METHODS
        name: @graph.getName element
        abstract: @graph.isAbstract element
        superTypes: @graph.getSuperTypes element
        attributes: @graph.getAttributes element
        inputs: @graph.getInputs element
        outputs: @graph.getOutputs element

    mMethods
  ###
