package side.onetime.dto.event.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Event;
import side.onetime.domain.enums.Category;

import java.util.List;
import java.util.UUID;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateEventRequest(
        String title,
        String startTime,
        String endTime,
        Category category,
        List<String> ranges
) {
    public Event toEntity() {
        return Event.builder()
                .eventId(UUID.randomUUID())
                .title(title)
                .startTime(startTime)
                .endTime(endTime)
                .category(category)
                .build();
    }
}