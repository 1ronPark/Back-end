package umc.lightup.member.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.lightup.member.validation.validator.ReadableDocumentFileValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReadableDocumentFileValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadableDocumentFile {
    String message() default "이미지 파일이 아닙니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}