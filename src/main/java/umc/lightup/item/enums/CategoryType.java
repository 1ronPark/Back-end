package umc.lightup.item.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.lightup.api.code.status.ErrorStatus;
import umc.lightup.exception.handler.GeneralHandler;
import umc.lightup.item.domain.ItemCategory;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CategoryType {
    PLATFORM("플랫폼"),
    LIFESTYLE("라이프스타일"),
    FINANCE("금융"),
    COMMUNITY("커뮤니티"),
    MEDIA("미디어"),
    EDUCATION("교육"),
    PRODUCTIVITY("생산성"),
    BLOCKCHAIN("블록체인"),
    NOCODE("노코드"),
    AI("인공지능"),
    DATA_ANALYSIS("데이터 분석"),
    DESIGN("디자인"),
    MARKETING("마케팅"),
    GAME("게임"),
    ECOMMERCE("이커머스"),
    HEALTH_CARE("헬스 케어"),
    BIO("바이오"),
    META_BUS("메타버스"),
    SALES("세일즈"),
    SECURITY("보안"),
    ESG("ESG"),
    ROBOTICS("로보틱스");

    private final String displayName;

    public static CategoryType toCategoryType(String displayName) {
        return Arrays.stream(values())
                .filter(c -> c.displayName.equals(displayName))
                .findAny()
                .orElseThrow(() -> new GeneralHandler(ErrorStatus.ITEM_CATEGORY_NOT_FOUND));
    }
}
