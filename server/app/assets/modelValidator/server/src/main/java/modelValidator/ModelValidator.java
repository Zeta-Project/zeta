package assets.modelValidator.server.src.main.java.modelValidator;

import org.mozilla.javascript.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ModelValidator implements IModelValidator {

    private static final String CREATE_WINDOW_COMMAND = "window = {};";
    private static final String JS_ROOT = "./shared/";
    private static final String[] JS_FILES = {
            JS_ROOT + "Constants.js",
            JS_ROOT + "JSONException.js",
            JS_ROOT + "Bounds.js",
            JS_ROOT + "Attribute.js",
            JS_ROOT + "Instance.js",
            JS_ROOT + "MetaModel.js",
            JS_ROOT + "ValidationResult.js",
            JS_ROOT + "ModelValidator.js"
    };

    private String metaModel;

    public ModelValidator () {
    }

    public ModelValidator (final String metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public ValidationResult validate (final String instance) {
        if (metaModel == null) {
            throw new IllegalStateException("Meta model is not initialized");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Instance must not be null");
        }

        ValidationResult result = null;
        Context context = Context.enter();

        try {

            Scriptable scope = setUpEnvironment(context);
            NativeObject validator = constructValidator(context, scope);
            NativeObject validationResult = callValidateMethod(context, scope, validator, instance);
            result = createValidationResult(validationResult);

        } catch (IOException e) {
            return null;
        } finally {
            Context.exit();
        }

        return result;

    }


    @Override
    public void setMetaModel (String metaModel) {
        this.metaModel = metaModel;
    }

    private Scriptable setUpEnvironment (Context context) throws IOException {
        Scriptable scope = context.initStandardObjects();
        context.evaluateString(scope, CREATE_WINDOW_COMMAND, "<cmd>", 1, null);
        for (String file : JS_FILES) {
            context.evaluateReader(scope, new FileReader(file), file, 1, null);
        }
        return scope;
    }

    private NativeObject constructValidator (Context context, Scriptable scope) {
        NativeObject window = (NativeObject) scope.get("window", scope);
        NativeFunction validatorConstructor = (NativeFunction) window.get("ModelValidator");
        return (NativeObject) validatorConstructor.construct(context, scope, new String[]{metaModel});
    }

    private NativeObject callValidateMethod (Context context, Scriptable scope, NativeObject validator, String instance) {
        NativeObject prototype = (NativeObject) validator.getPrototype();
        Function validate = (Function) prototype.get("validate");
        return (NativeObject) validate.call(context, scope, validator, new String[]{instance});
    }

    private ValidationResult createValidationResult (NativeObject validationResult) {
        boolean valid = (Boolean) validationResult.get("valid");
        List<String> messages = new LinkedList<>();
        ((List<?>) validationResult.get("messages")).forEach(message -> messages.add(Context.toString(message)));
        return new ValidationResult(valid, messages);
    }

}
