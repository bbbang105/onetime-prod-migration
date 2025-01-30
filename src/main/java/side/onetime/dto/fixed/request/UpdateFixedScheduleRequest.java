package side.onetime.dto.fixed.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import side.onetime.dto.fixed.response.FixedScheduleResponse;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateFixedScheduleRequest(
        @NotNull(message = "스케줄 목록은 필수 값입니다.")
        List<FixedScheduleResponse> schedules
) {
}
