package side.onetime.dto.fixed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetFixedScheduleResponse(
        List<FixedScheduleResponse> schedules
) {
        public static GetFixedScheduleResponse from(List<FixedScheduleResponse> schedules) {
                return new GetFixedScheduleResponse(schedules);
        }
}
