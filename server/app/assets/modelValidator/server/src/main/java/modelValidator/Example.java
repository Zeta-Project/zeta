package assets.modelValidator.server.src.main.java.modelValidator;

import org.mozilla.javascript.JavaScriptException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Example {

    private static final String EXAMPLE_METAMODEL_PATH = "./server/examples/metamodel.json";
    private static final String EXAMPLE_INSTANCE_PATH = "./server/examples/instance.json";

    public static void main (final String[] args) throws IOException {

        try {
            String metaModel = new String(Files.readAllBytes(Paths.get(EXAMPLE_METAMODEL_PATH)), StandardCharsets.UTF_8);
            String instance = new String(Files.readAllBytes(Paths.get(EXAMPLE_INSTANCE_PATH)), StandardCharsets.UTF_8);

            // Call the validator
            IModelValidator validator = new ModelValidator(metaModel);
            ValidationResult result = validator.validate(instance);

            // Print the results
            System.out.println("valid: " + result.isValid());
            result.getMessages().forEach(System.out::println);

        } catch (JavaScriptException e) {
            System.err.println("Syntax error in meta model or instance JSON");
        }
    }

}
