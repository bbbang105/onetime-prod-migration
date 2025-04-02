package side.onetime.dto.admin.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Banner;

import java.time.format.DateTimeFormatter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetActivatedBannerResponse(
        Long id,
        String contentKor,
        String contentEng,
        String backgroundColorCode,
        String textColorCode,
        Boolean isActivated,
        String createdDate,
        String linkUrl
) {
    public static GetActivatedBannerResponse from(Banner banner) {
        return new GetActivatedBannerResponse(
                banner.getId(),
                banner.getContentKor(),
                banner.getContentEng(),
                banner.getBackgroundColorCode(),
                banner.getTextColorCode(),
                banner.getIsActivated(),
                banner.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                banner.getLinkUrl()
        );
    }
}
