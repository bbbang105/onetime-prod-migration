package side.onetime.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import side.onetime.domain.Banner;
import side.onetime.domain.enums.Language;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegisterBannerRequest(

        @Size(max = 200, message = "내용은 최대 200자까지 가능합니다.")
        String content,

        @Size(max = 30, message = "색상 값은 최대 200자까지 가능합니다.")
        String colorCode,

        @NotNull(message = "언어는 필수 값입니다.")
        Language language
) {

        public Banner toEntity() {
                return Banner.builder()
                        .content(content)
                        .colorCode(colorCode)
                        .language(language)
                        .build();
        }
}
