package side.onetime.dto.schedule.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonProperty;
import side.onetime.domain.Selection;

import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DateSchedule(
        @JsonProperty("time_point") String date,
        List<String> times
) {
    public static DateSchedule from(List<Selection> selections) {
        List<String> times = new ArrayList<>();
        for (Selection selection : selections) {
            times.add(selection.getSchedule().getTime());
        }
        return new DateSchedule(selections.get(0).getSchedule().getDate(), times);
    }
}
