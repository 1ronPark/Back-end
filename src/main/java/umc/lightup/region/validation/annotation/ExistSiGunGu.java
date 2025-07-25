package umc.lightup.region.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.lightup.region.validation.validator.SiGunGuExistValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SiGunGuExistValidator.class)
@Target( {ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistSiGunGu {

    String message() default "해당하는 시/군/구가 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
