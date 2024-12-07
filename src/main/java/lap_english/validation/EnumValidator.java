package lap_english.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    private Pattern pattern;

    @Override
    public void initialize(ValidEnum enumPattern) {
        try {
            pattern = Pattern.compile(enumPattern.regexp()); // lay tu value cua regex truyen vao @EnumPattern
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Given regex is invalid", e);
        }
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        Matcher matcher = pattern.matcher(value.name());
        return matcher.matches();
    }
}
