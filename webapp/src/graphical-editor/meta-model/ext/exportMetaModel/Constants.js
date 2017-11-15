
/*
  Provides all constants for the field-names of the exported meta model.
 */

export default (function() {
  function Constants() {}

  Constants.CLASS = 'mClass';

  Constants.REFERENCE = 'mReference';

  Constants.ENUM = 'mEnum';

  Constants.ATTRIBUTE = 'mAttribute';

  Constants.METHOD = 'mMethod';

  Constants.field = {
    ATTRIBUTES: 'm_attributes',
    METHODS: 'm_methods',
    LINKDEF_INPUT: 'linkdef_input',
    LINKDEF_OUTPUT: 'linkdef_output',
    LINKDEF_SOURCE: 'linkdef_source',
    LINKDEF_TARGET: 'linkdef_target',
    UPPER_BOUND: 'upperBound',
    LOWER_BOUND: 'lowerBound',
    TARGET_DELETION_DELETES_SOURCE: 'targetDeletionDeletesSource',
    SOURCE_DELETION_DELETES_TARGET: 'sourceDeletionDeletesTarget',
    DELETE_IF_LOWER: 'deleteIfLower'
  };

  return Constants;
})();
