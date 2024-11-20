package lap_english.util;

import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ObjectsValidator<T> {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate(T object, Class<?> group) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, group);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public boolean isValidField(T object, String field, Class<?> group) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(object, field, group);
        return violations.isEmpty();
    }
}
