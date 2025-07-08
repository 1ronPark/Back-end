package umc.lightup.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.lightup.api.code.BaseErrorCode;
import umc.lightup.api.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
