package side.onetime.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import side.onetime.domain.enums.Language;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateBannerRequest(
        @Size(max = 50, message = "제목은 최대 50자까지 가능합니다.")
        String title,

        @Size(max = 200, message = "내용은 최대 200자까지 가능합니다.")
        String content,

        @Size(max = 30, message = "색상 값은 최대 30자까지 가능합니다.")
        String colorCode,

        Language language,

        Boolean isActivated
) {
}
