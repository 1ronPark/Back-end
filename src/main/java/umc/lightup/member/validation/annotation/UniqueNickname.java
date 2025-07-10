package umc.lightup.member.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.lightup.member.validation.validator.UniqueNicknameValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueNicknameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNickname {
    String message() default "이미 존재하는 닉네임입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}