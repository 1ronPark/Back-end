package umc.lightup.api.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.lightup.api.code.BaseErrorCode;
import umc.lightup.api.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER4000", "유효하지 않은 토큰입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4002", "패스워드가 불일치합니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4003", "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4004", "이미 존재하는 이메일입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "MEMBER4005", "이미 존재하는 휴대폰 번호입니다."),
    NO_CREDENTIAL(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER5000", "저장된 패스워드가 없습니다."),

    //Notification 관련 에러
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4001", "해당 알림이 존재하지 않습니다. "),
  
    //Position 관련 에러
    POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "POSITION4000", "포지션이 존재하지 않습니다."),

    //Skill 관련 에러
    DUPLICATED_SKILL_NAME(HttpStatus.BAD_REQUEST, "SKILL4000", "이미 존재하는 스킬 이름입니다."),
    SKILL_NOT_FOUND(HttpStatus.NOT_FOUND, "SKILL4001", "스킬이 존재하지 않습니다."),
    DUPLICATED_SKILL_SELECT(HttpStatus.BAD_REQUEST, "SKILL4002", "이미 선택한 스킬입니다."),

    //Strength 관련 에러
    DUPLICATED_STRENGTH_NAME(HttpStatus.BAD_REQUEST, "STRENGTH4000", "이미 존재하는 강점 이름입니다."),
    STRENGTH_NOT_FOUND(HttpStatus.NOT_FOUND, "STRENGTH4001", "강점이 존재하지 않습니다."),
    DUPLICATED_STRENGTH_SELECT(HttpStatus.BAD_REQUEST, "STRENGTH4002", "이미 선택한 강점입니다.")
    ;



    private final HttpStatus httpStatus;
    private String code;
    private String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
