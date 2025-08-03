package umc.lightup.position.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.position.validation.annotation.ExistPosition;
import umc.lightup.position.service.PositionService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PositionExistValidator implements ConstraintValidator<ExistPosition, Long> {

    private final PositionService positionService;

    @Override
    public void initialize(ExistPosition constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = positionService.isPositionExist(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.POSITION_NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
