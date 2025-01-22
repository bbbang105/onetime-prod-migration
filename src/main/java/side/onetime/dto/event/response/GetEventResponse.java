package side.onetime.dto.event.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Event;
import side.onetime.domain.enums.Category;
import side.onetime.domain.enums.EventStatus;

import java.util.List;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetEventResponse(
        String eventId,
        String title,
        String startTime,
        String endTime,
        Category category,
        List<String> ranges,
        EventStatus eventStatus
) {
    public static GetEventResponse of(Event event, List<String> ranges, EventStatus eventStatus) {
        return new GetEventResponse(
                String.valueOf(event.getEventId()),
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCategory(),
                ranges,
                EventStatus.PARTICIPANT == eventStatus ? eventStatus : EventStatus.CREATOR
        );
    }
}
