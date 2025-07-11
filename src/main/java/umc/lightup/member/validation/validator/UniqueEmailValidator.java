package umc.lightup.member.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.member.validation.annotation.UniqueEmail;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final MemberCommandService memberCommandService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = !memberCommandService.isEmailExist(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DUPLICATE_EMAIL.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
