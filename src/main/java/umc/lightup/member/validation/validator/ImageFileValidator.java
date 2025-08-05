package umc.lightup.member.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.member.validation.annotation.ImageFile;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {
    @Override
    public void initialize(ImageFile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (value != null) {
            String contentType = value.getContentType();
            isValid = contentType != null && contentType.startsWith("image/");
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NOT_IMAGE.toString()).addConstraintViolation();
        }

        return isValid;
    }
}