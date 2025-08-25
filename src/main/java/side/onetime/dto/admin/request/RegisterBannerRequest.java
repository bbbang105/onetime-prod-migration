package side.onetime.dto.admin.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import side.onetime.domain.Banner;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RegisterBannerRequest(

        @Size(max = 200, message = "조직명은 최대 200자까지 가능합니다.")
        String organization,

        @Size(max = 200, message = "제목은 최대 200자까지 가능합니다.")
        String title,

        @Size(max = 200, message = "부제목은 최대 200자까지 가능합니다.")
        String subTitle,

        @Size(max = 30, message = "색상 값은 최대 30자까지 가능합니다.")
        String colorCode,

        @Size(max = 200, message = "링크 URL은 최대 200자까지 가능합니다.")
        String linkUrl
) {

    public Banner toEntity() {
        return Banner.builder()
                .organization(organization)
                .title(title)
                .subTitle(subTitle)
                .colorCode(colorCode)
                .linkUrl(linkUrl)
                .build();
    }
}
