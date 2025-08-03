package umc.lightup.region.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.region.service.RegionService;
import umc.lightup.region.validation.annotation.ExistSiDo;

@Component
@RequiredArgsConstructor
public class SiDoExistValidator implements ConstraintValidator<ExistSiDo, String> {

    private final RegionService regionService;

    @Override
    public void initialize(ExistSiDo constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true;
        boolean isValid = regionService.isSiDoExist(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.REGION_NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
