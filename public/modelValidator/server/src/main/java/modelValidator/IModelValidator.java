package modelValidator;

public interface IModelValidator {
    ValidationResult validate (String instance);
    void setMetaModel (String metaModel);
}
