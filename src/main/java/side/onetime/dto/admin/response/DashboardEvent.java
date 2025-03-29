package side.onetime.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Event;
import side.onetime.domain.Schedule;
import side.onetime.domain.enums.Category;
import side.onetime.util.DateUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DashboardEvent(
        Long id,
        String eventId,
        String title,
        String startTime,
        String endTime,
        Category category,
        int participantCount,
        String createdDate,
        List<String> ranges
) {
    public static DashboardEvent of(Event event, List<Schedule> schedules, int participantCount) {
        List<String> ranges = event.getCategory() == Category.DATE
                ? DateUtil.getSortedDateRanges(schedules.stream().map(Schedule::getDate).toList(), "yyyy.MM.dd")
                : DateUtil.getSortedDayRanges(schedules.stream().map(Schedule::getDay).toList());

        return new DashboardEvent(
                event.getId(),
                String.valueOf(event.getEventId()),
                event.getTitle(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCategory(),
                participantCount,
                event.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                ranges
        );
    }
}
