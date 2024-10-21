package side.onetime.dto.member.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IsDuplicateRequest(
        @NotBlank(message = "Event ID는 필수 값입니다.") String eventId,
        @NotBlank(message = "이름은 필수 값입니다.") String name
) {}