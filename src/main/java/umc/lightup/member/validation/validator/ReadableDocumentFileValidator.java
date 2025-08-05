package umc.lightup.member.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.member.validation.annotation.ReadableDocumentFile;

import java.util.Set;

@Component
public class ReadableDocumentFileValidator implements ConstraintValidator<ReadableDocumentFile, MultipartFile> {

    private final Set<String> readableDocumentContentTypes = Set.of(
            "text/plain", // 일반 텍스트 파일
            "text/html", // HTML 파일
            "application/pdf", // PDF 문서
            "application/msword", // MS Word 문서, .doc
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // MS Word 문서, .docx
            "application/vnd.ms-powerpoint", // MS PowerPoint 프레젠테이션, .ppt
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" // MS PowerPoint 프레젠테이션, .pptx
    ); // word나 ppt를 pdf로 변경하는 기술이 존재하긴 할텐데 일단 그냥 둠
    // 이 파일 목록은 프론트와의 협의가 필요함

    @Override
    public void initialize(ReadableDocumentFile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (value != null) {
            String contentType = value.getContentType();
            isValid = contentType != null && readableDocumentContentTypes.contains(contentType);
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NOT_READABLE_FILE.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
