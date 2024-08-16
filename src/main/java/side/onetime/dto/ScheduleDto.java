package side.onetime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.domain.Member;
import side.onetime.domain.Selection;

import java.time.LocalTime;
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
        private List<DaySchedule> daySchedules;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DaySchedule {
        private String day;
        private List<LocalTime> times;

        public static DaySchedule of(List<Selection> selections) {
            List<LocalTime> times = new ArrayList<>();
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
        private List<DateSchedule> dateSchedules;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DateSchedule {
        private String date;
        private List<LocalTime> times;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PerDaySchedulesResponse {
        private String name;
        private List<DaySchedule> daySchedules;

        public static PerDaySchedulesResponse of(Member member, List<DaySchedule> daySchedules) {
            return PerDaySchedulesResponse.builder()
                    .name(member.getName())
                    .daySchedules(daySchedules)
                    .build();
        }
    }
}
