package umc.lightup.api.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorReasonDTO {

    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private String code;
    private String message;

    public boolean isSuccess() {
        return isSuccess;
    }
}
