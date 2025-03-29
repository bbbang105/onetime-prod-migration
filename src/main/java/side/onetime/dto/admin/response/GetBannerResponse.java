package side.onetime.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Banner;
import side.onetime.domain.enums.Language;

import java.time.format.DateTimeFormatter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetBannerResponse(
        Long id,
        String title,
        String content,
        String colorCode,
        Language language,
        Boolean isActivated,
        String createdDate
) {
    public static GetBannerResponse from(Banner banner) {
        return new GetBannerResponse(
                banner.getId(),
                banner.getTitle(),
                banner.getContent(),
                banner.getColorCode(),
                banner.getLanguage(),
                banner.getIsActivated(),
                banner.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }
}
