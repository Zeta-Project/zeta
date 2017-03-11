package assets.modelValidator.server.src.main.java.modelValidator;


import java.util.List;

public class ValidationResult {

    private final boolean valid;
    private final List<String> messages;

    public ValidationResult (boolean valid, List<String> messages) {
        this.valid = valid;
        this.messages = messages;
    }

    public boolean isValid () {
        return valid;
    }

    public List<String> getMessages () {
        return messages;
    }

}
