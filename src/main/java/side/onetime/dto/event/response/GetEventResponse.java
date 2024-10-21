package side.onetime.dto.event.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Event;
import side.onetime.domain.enums.Category;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetEventResponse(
        String title,
        String startTime,
        String endTime,
        Category category,
        List<String> ranges
) {
    public static GetEventResponse of(Event event, List<String> ranges) {
        return new GetEventResponse(
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCategory(),
                ranges
        );
    }
}