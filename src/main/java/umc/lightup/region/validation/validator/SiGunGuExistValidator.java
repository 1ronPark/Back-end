package umc.lightup.region.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.region.service.RegionService;
import umc.lightup.region.validation.annotation.ExistSiGunGu;

@Component
@RequiredArgsConstructor
public class SiGunGuExistValidator implements ConstraintValidator<ExistSiGunGu, String> {

    private final RegionService regionService;

    @Override
    public void initialize(ExistSiGunGu constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true;

        boolean isValid = regionService.isSiGunGuExist(value);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.REGION_NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
