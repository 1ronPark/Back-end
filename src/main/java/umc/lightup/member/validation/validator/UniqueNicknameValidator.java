package umc.lightup.member.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.member.service.MemberCommandService;
import umc.lightup.member.validation.annotation.UniqueNickname;

@Component
@RequiredArgsConstructor
public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {

    private final MemberCommandService memberCommandService;

    @Override
    public void initialize(UniqueNickname constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // null 여부는 여기서는 확인 안함
        boolean isValid = !memberCommandService.isNicknameExist(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DUPLICATE_NICKNAME.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
