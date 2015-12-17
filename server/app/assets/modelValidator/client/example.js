(function () {
    var txtMetaModel = document.getElementById('txt-metaModel');
    var txtInstance = document.getElementById('txt-instance');
    var buttonValidate = document.getElementById('btn-validate');
    var inpValid = document.getElementById('inp-valid');
    var txtOutput = document.getElementById('txt-output');

    txtMetaModel.value = JSON.stringify(exampleMetaModel, null, '  ');
    txtInstance.value = JSON.stringify(exampleInstance, null, '  ');

    buttonValidate.onclick = function () {

        // Call the validator
        try {
            var validator = new ModelValidator(txtMetaModel.value);
            var result = validator.validate(txtInstance.value);

            // Print the results
            inpValid.value = result.isValid();
            txtOutput.value = result.getMessages().join('\n') || 'No errors.';
        } catch (e) {
            txtOutput.value = e.message;
        }
    };
})();