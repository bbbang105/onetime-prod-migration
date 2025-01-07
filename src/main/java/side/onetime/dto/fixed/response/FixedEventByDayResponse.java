package side.onetime.dto.fixed.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FixedEventByDayResponse(
        Long id,
        String title,
        String startTime,
        String endTime
) {
    public static FixedEventByDayResponse of(Long id, String title, String startTime, String endTime) {
        return new FixedEventByDayResponse(
                id,
                title,
                startTime,
                endTime
        );
    }
}
