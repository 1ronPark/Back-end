package umc.lightup.position.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.lightup.position.validation.validator.PositionExistValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositionExistValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistPosition {

    String message() default "해당하는 포지션이 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
