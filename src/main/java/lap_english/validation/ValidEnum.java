package lap_english.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
    String name();

    String regexp();

    String message() default "{name} must match {regexp}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
