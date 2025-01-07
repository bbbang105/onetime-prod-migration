package side.onetime.dto.fixed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FixedEventDetailResponse(
        String title,
        List<FixedScheduleResponse> schedules
) {
    public static FixedEventDetailResponse of(String title, List<FixedScheduleResponse> schedules) {
        return new FixedEventDetailResponse(
                title,
                schedules
        );
    }
}
