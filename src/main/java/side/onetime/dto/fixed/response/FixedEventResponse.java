package side.onetime.dto.fixed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FixedEventResponse(
        Long id,
        List<FixedScheduleResponse> schedules
) {
    public static FixedEventResponse of(Long id, List<FixedScheduleResponse> schedules) {
        return new FixedEventResponse(
                id,
                schedules
        );
    }
}
