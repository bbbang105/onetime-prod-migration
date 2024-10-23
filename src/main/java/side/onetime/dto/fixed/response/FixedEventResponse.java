package side.onetime.dto.fixed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FixedEventResponse(
        Long id,
        String title,
        String startTime,
        String endTime,
        List<FixedScheduleResponse> schedules
) {
    public static FixedEventResponse of (Long id, String title, String startTime, String endTime, List<FixedScheduleResponse> schedules) {
        return new FixedEventResponse(
                id,
                title,
                startTime,
                endTime,
                schedules
        );
    }
}