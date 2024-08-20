package side.onetime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.domain.Schedule;
import side.onetime.global.common.constant.Category;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetParticipantsResponse {
        private List<String> names;

        public static GetParticipantsResponse of(List<Member> members) {
            List<String> names = members.stream()
                    .map(Member::getName)
                    .collect(Collectors.toList());

            return GetParticipantsResponse.builder()
                    .names(names)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetMostPossibleTime {
        private String timePoint;
        private String startTime;
        private String endTime;
        private int possibleCount;
        private List<String> possibleNames;
        private List<String> impossibleNames;

        public static GetMostPossibleTime dayOf(Schedule schedule, List<String> possibleNames, List<String> impossibleNames) {
            return GetMostPossibleTime.builder()
                    .timePoint(schedule.getDay())
                    .startTime(schedule.getTime())
                    .endTime(String.valueOf(LocalTime.parse(schedule.getTime()).plusMinutes(30)))
                    .possibleCount(possibleNames.size())
                    .possibleNames(possibleNames)
                    .impossibleNames(impossibleNames)
                    .build();
        }

        public static GetMostPossibleTime dateOf(Schedule schedule, List<String> possibleNames, List<String> impossibleNames) {
            return GetMostPossibleTime.builder()
                    .timePoint(schedule.getDate())
                    .startTime(schedule.getTime())
                    .endTime(String.valueOf(LocalTime.parse(schedule.getTime()).plusMinutes(30)))
                    .possibleCount(possibleNames.size())
                    .possibleNames(possibleNames)
                    .impossibleNames(impossibleNames)
                    .build();
        }

        public void updateEndTime(String endTime) {
            endTime = String.valueOf(LocalTime.parse(endTime).plusMinutes(30));
            this.endTime = endTime;
        }
    }
}