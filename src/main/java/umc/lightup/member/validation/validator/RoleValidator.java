package umc.lightup.member.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.member.enums.Role;
import umc.lightup.member.validation.annotation.ValidRole;

public class RoleValidator implements ConstraintValidator<ValidRole, Role> {
    @Override
    public void initialize(ValidRole constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Role value, ConstraintValidatorContext context) {
        boolean isValid = value == null || value.equals(Role.LEADER) || value.equals(Role.TEAMMATE);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.INVALID_ROLE.toString()).addConstraintViolation();
        }

        return isValid;
    }
}