package side.onetime.dto.schedule.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PerDaySchedulesResponse(
        String name,
        @JsonProperty("schedules") List<DaySchedule> daySchedules
) {
    public static PerDaySchedulesResponse of(String name, List<DaySchedule> daySchedules) {
        return new PerDaySchedulesResponse(name, daySchedules);
    }
}
