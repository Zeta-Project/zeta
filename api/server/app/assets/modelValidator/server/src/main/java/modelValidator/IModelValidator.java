package assets.modelValidator.server.src.main.java.modelValidator;

public interface IModelValidator {
    ValidationResult validate (String instance);
    void setMetaModel (String metaModel);
}
