package side.onetime.dto.fixed.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.dto.fixed.response.FixedScheduleResponse;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ModifyFixedEventRequest(
        String title,
        List<FixedScheduleResponse> schedules
) {
}
