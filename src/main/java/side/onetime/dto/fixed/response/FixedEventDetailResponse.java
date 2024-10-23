package side.onetime.dto.fixed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FixedEventDetailResponse(
        String title,
        String startTime,
        String endTime,
        List<FixedScheduleResponse> schedules
) {
    public static FixedEventDetailResponse of(String title, String startTime, String endTime, List<FixedScheduleResponse> schedules) {
        return new FixedEventDetailResponse(
                title,
                startTime,
                endTime,
                schedules
        );
    }
}