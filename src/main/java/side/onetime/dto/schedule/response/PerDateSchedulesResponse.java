package side.onetime.dto.schedule.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PerDateSchedulesResponse(
        String name,
        @JsonProperty("schedules") List<DateSchedule> dateSchedules
) {
    public static PerDateSchedulesResponse of(String name, List<DateSchedule> dateSchedules) {
        return new PerDateSchedulesResponse(name, dateSchedules);
    }
}
