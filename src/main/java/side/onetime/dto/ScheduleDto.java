package side.onetime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.domain.Member;
import side.onetime.domain.Selection;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateDayScheduleRequest {
        private String eventId;
        private String memberId;
        @JsonProperty("schedules")
        private List<DaySchedule> daySchedules;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DaySchedule {
        @JsonProperty("time_point")
        private String day;
        private List<String> times;

        public static DaySchedule of(List<Selection> selections) {
            List<String> times = new ArrayList<>();
            for (Selection selection : selections) {
                times.add(selection.getSchedule().getTime());
            }
            return DaySchedule.builder()
                    .day(selections.get(0).getSchedule().getDay())
                    .times(times)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateDateScheduleRequest {
        private String eventId;
        private String memberId;
        @JsonProperty("schedules")
        private List<DateSchedule> dateSchedules;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DateSchedule {
        @JsonProperty("time_point")
        private String date;
        private List<String> times;

        public static DateSchedule of(List<Selection> selections) {
            List<String> times = new ArrayList<>();
            for (Selection selection : selections) {
                times.add(selection.getSchedule().getTime());
            }
            return DateSchedule.builder()
                    .date(selections.get(0).getSchedule().getDate())
                    .times(times)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PerDaySchedulesResponse {
        private String name;
        @JsonProperty("schedules")
        private List<DaySchedule> daySchedules;

        public static PerDaySchedulesResponse of(Member member, List<DaySchedule> daySchedules) {
            return PerDaySchedulesResponse.builder()
                    .name(member.getName())
                    .daySchedules(daySchedules)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PerDateSchedulesResponse {
        private String name;
        @JsonProperty("schedules")
        private List<DateSchedule> dateSchedules;

        public static PerDateSchedulesResponse of(Member member, List<DateSchedule> dateSchedules) {
            return PerDateSchedulesResponse.builder()
                    .name(member.getName())
                    .dateSchedules(dateSchedules)
                    .build();
        }
    }
}
