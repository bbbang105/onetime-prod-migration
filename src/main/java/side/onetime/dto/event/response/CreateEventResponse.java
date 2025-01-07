package side.onetime.dto.event.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Event;

import java.util.UUID;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateEventResponse(
        UUID eventId
) {
    public static CreateEventResponse of(Event event) {
        return new CreateEventResponse(event.getEventId());
    }
}
