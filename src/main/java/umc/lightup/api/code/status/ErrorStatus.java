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
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "MEMBER4006", "올바른 Role이 아닙니다. LEADER, TEAMMATE 중 하나를 선택해 주세요."),
    NOT_IMAGE(HttpStatus.BAD_REQUEST, "MEMBER4008", "이미지 파일이 아닙니다."), //MEMBER4007 에러를 만들었던 기억이 있는데 왜 없지?
    NO_CREDENTIAL(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER5000", "저장된 패스워드가 없습니다."),

    // Credential 관련 에러
    CREDENTIAL_NOT_FOUND(HttpStatus.BAD_REQUEST, "CREDENTIAL4000", "해당 형태의 로그인 정보를 찾을 수 없습니다."),
    ALREADY_SIGNED_IN_EMAIL(HttpStatus.BAD_REQUEST, "CREDENTIAL4001", "이미 동일한 이메일로 가입된 회원이 있습니다."),
    CREDENTIAL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "CREDENTIAL4002", "이미 해당 방법으로 소셜로그인이 연결되어 있습니다."),
    CREDENTIAL_ALREADY_USED(HttpStatus.BAD_REQUEST, "CREDENTIAL4003", "이미 다른 계정에 소셜로그인이 연결되어 있습니다."),
    ONLY_CREDENTIAL_REMAIN(HttpStatus.BAD_REQUEST, "CREDENTIAL4004", "현재 유일한 로그인 방법이 하나밖에 없습니다. 로그인 방법을 지우면 로그인이 불가합니다."),
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "CREDENTIAL4005", "auth code가 올바르지 않습니다."),
    AUTH_NOT_GRANTED(HttpStatus.BAD_REQUEST, "CREDENTIAL4006", "소셜로그인의 권한이 부족합니다. 필요한 모든 권한 제공에 동의했는지 확인해 주세요."),

    //Notification 관련 에러
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4001", "해당 알림이 존재하지 않습니다. "),
  
    //MemberLike 관련 에러
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "MEMBER4100", "이미 좋아요 한 회원입니다. 좋아요를 취소하려면 Delete Method로 요청을 보내 주세요."),
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4101", "좋아요하지 않은 회원입니다. 좋아요를 하려면 Post Method로 요청을 보내 주세요."),
    SELF_LIKE(HttpStatus.BAD_REQUEST, "MEMBER4102", "회원 자신에 대한 좋아요 요청입니다."),

    //PortFolio 관련 에러
    NOT_READABLE_FILE(HttpStatus.BAD_REQUEST, "MEMBER4200", "읽을 수 있는 파일 형태가 아닙니다."),
    PORTFOLIO_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4201", "포트폴리오를 찾을 수 없습니다."),

    //Region 관련 에러
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION4000", "해당 지역이 존재하지 않습니다."),
  
    //Position 관련 에러
    POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "POSITION4000", "포지션이 존재하지 않습니다."),

    //Skill 관련 에러
    DUPLICATED_SKILL_NAME(HttpStatus.BAD_REQUEST, "SKILL4000", "이미 존재하는 스킬 이름입니다."),
    SKILL_NOT_FOUND(HttpStatus.NOT_FOUND, "SKILL4001", "스킬이 존재하지 않습니다."),
    DUPLICATED_SKILL_SELECT(HttpStatus.BAD_REQUEST, "SKILL4002", "이미 선택한 스킬입니다."),

    //Strength 관련 에러
    DUPLICATED_STRENGTH_NAME(HttpStatus.BAD_REQUEST, "STRENGTH4000", "이미 존재하는 강점 이름입니다."),
    STRENGTH_NOT_FOUND(HttpStatus.NOT_FOUND, "STRENGTH4001", "강점이 존재하지 않습니다."),
    DUPLICATED_STRENGTH_SELECT(HttpStatus.BAD_REQUEST, "STRENGTH4002", "이미 선택한 강점입니다."),

    //Item 관련 에러
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM4000", "프로젝트가 존재하지 않습니다."),
    ITEM_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "ITEM4001", "프로젝트를 수정할 권한이 없습니다."),
    NOT_MY_ITEM(HttpStatus.FORBIDDEN, "ITEM4002", "내 프로젝트가 아닙니다."),

    //ItemLike 관련 에러
    MY_ITEM_LIKE(HttpStatus.BAD_REQUEST, "ITEMLIKE4000", "자신의 프로젝트에는 좋아요를 누를 수 없습니다."),
    ITEM_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "ITEMLIKE4001", "이미 좋아요를 누른 프로젝트입니다."),
    ITEM_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEMLIKE4002", "좋아요가 존재하지 않습니다."),

    //ItemApply 관련 에러
    DUPLICATE_ITEM_APPLY(HttpStatus.BAD_REQUEST, "ITEMAPPLY4000", "이미 지원한 프로젝트입니다."),

    //ItemComment 관련 에러
    ITEM_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEMCOMMENT4000", "해당 댓글이 존재하지 않습니다."),

    //ItemViewHistory 관련 에러
    ITEM_VIEW_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEMVIEWHISTORY4000", "프로젝트 조회 내역이 존재하지 않습니다."),

    //ItemImage 관련 에러
    ITEM_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM_IMAGE4000", "아이템 이미지가 존재하지 않습니다."),

    //ItemCategory 관련 에러
    ITEM_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEMCATEGORY4000", "해당 아이템 카테고리가 존재하지 않습니다."),

    //MemberPosition 관련 에러
    MEMBER_POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_POSITION4000", "해당 포지션을 선택하지 않았습니다."),
    DUPLICATED_POSITION_SELECT(HttpStatus.NOT_FOUND, "MEMBER_POSITION4001", "이미 선택한 포지션입니다."),

    //MemberSkill 관련 에러
    MEMBER_SKILL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_SKILL4000", "유저의 스킬이 존재하지 않습니다."),

    //MemberStrength 관련 에러
    MEMBER_STRENGTH_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_STRENGTH4000", "유저의 강점이 존재하지 않습니다."),

    //MemberRegion 관련 에러
    MEMBER_REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_REGION4000", "선택 지역이 존재하지 않습니다."),

    //Email 관련 에러
    EMAIL_SEND_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "EMAIL5003", "이메일 전송에 실패했습니다."),

    //Post 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST4000", "포스트가 존재하지 않습니다."),

    //PostImage 관련 에러
    TOO_MANY_POST_IMAGE(HttpStatus.BAD_REQUEST, "POST_IMAGE4000", "이미지는 3개까지만 업로드 가능합니다."),

    //PostComment 관련 에러
    POST_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_COMMENT4000", "해당 포스트의 댓글이 존재하지 않습니다."),

    //PostLike 관련 에러
    MY_POST_LIKE(HttpStatus.BAD_REQUEST, "POSTLIKE4000", "자신의 포스트에는 좋아요를 누를 수 없습니다."),
    POST_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "POSTLIKE4001", "이미 좋아요를 누른 포스트입니다."),
    POST_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "POSTLIKE4002", "포스트 좋아요가 존재하지 않습니다."),

    //CommentLike 관련 에러
    MY_COMMENT_LIKE(HttpStatus.BAD_REQUEST, "COMMENTLIKE4000", "자신의 댓글에는 좋아요를 누를 수 없습니다."),
    COMMENT_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "COMMENTLIKE4001", "이미 좋아요를 누른 댓글입니다."),
    COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENTLIKE4002", "해당 댓글 좋아요가 존재하지 않습니다.")
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
