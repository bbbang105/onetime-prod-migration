package side.onetime.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import side.onetime.domain.Banner;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RegisterBannerRequest(

        @Size(max = 200, message = "한글 내용은 최대 200자까지 가능합니다.")
        String contentKor,

        @Size(max = 200, message = "영문 내용은 최대 200자까지 가능합니다.")
        String contentEng,

        @Size(max = 30, message = "배경 색상 값은 최대 30자까지 가능합니다.")
        String backgroundColorCode,

        @Size(max = 30, message = "텍스트 색상 값은 최대 30자까지 가능합니다.")
        String textColorCode
) {

        public Banner toEntity() {
                return Banner.builder()
                        .contentKor(contentKor)
                        .contentEng(contentEng)
                        .backgroundColorCode(backgroundColorCode)
                        .textColorCode(textColorCode)
                        .build();
        }
}
