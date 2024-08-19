package side.onetime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.domain.Event;
import side.onetime.global.common.constant.Category;

import java.util.List;
import java.util.UUID;

public class EventDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateEventRequest {
        private String title;
        private String startTime;
        private String endTime;
        private Category category;
        private List<String> ranges;

        public Event to() {
            return Event.builder()
                    .eventId(UUID.randomUUID())
                    .title(title)
                    .startTime(startTime)
                    .endTime(endTime)
                    .category(category)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateEventResponse {
        private UUID eventId;

        public static CreateEventResponse of(Event event) {
            return CreateEventResponse.builder()
                    .eventId(event.getEventId())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetEventResponse {
        private String title;
        private String startTime;
        private String endTime;
        private Category category;
        private List<String> ranges;

        public static GetEventResponse of(Event event, List<String> ranges) {
            return GetEventResponse.builder()
                    .title(event.getTitle())
                    .startTime(event.getStartTime())
                    .endTime(event.getEndTime())
                    .category(event.getCategory())
                    .ranges(ranges)
                    .build();
        }
    }
}
