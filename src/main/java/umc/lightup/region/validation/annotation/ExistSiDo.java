package umc.lightup.region.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.lightup.region.validation.validator.SiDoExistValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SiDoExistValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistSiDo {

    String message() default "해당하는 시/도가 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
