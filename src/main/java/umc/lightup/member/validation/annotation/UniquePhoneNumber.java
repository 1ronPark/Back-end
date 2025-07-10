package umc.lightup.member.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.lightup.member.validation.validator.UniquePhoneNumberValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePhoneNumberValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePhoneNumber {
    String message() default "이미 존재하는 휴대폰 번호입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
