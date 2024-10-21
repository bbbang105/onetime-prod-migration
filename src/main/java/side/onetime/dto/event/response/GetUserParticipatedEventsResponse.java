package side.onetime.dto.event.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Event;
import side.onetime.domain.EventParticipation;
import side.onetime.domain.enums.Category;

import java.util.List;
import java.util.UUID;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetUserParticipatedEventsResponse(
        UUID eventId,
        Category category,
        String title,
        String createdDate,
        int participantCount,
        String eventStatus,
        List<GetMostPossibleTime> mostPossibleTimes
) {
    public static GetUserParticipatedEventsResponse of(Event event, EventParticipation eventParticipation, int participantCount, List<GetMostPossibleTime> mostPossibleTimes) {
        return new GetUserParticipatedEventsResponse(
                event.getEventId(),
                event.getCategory(),
                event.getTitle(),
                String.valueOf(event.getCreatedDate()),
                participantCount,
                String.valueOf(eventParticipation.getEventStatus()),
                mostPossibleTimes
        );
    }
}