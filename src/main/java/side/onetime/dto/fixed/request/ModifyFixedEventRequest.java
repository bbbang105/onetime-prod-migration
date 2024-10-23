package side.onetime.dto.fixed.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;
import side.onetime.dto.fixed.response.FixedScheduleResponse;

import java.util.List;

import static side.onetime.util.DateUtil.addThirtyMinutes;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ModifyFixedEventRequest(
        String title,
        List<FixedScheduleResponse> schedules
) {
    public FixedEvent toEntity(User user, String startTime, String endTime) {
        return FixedEvent.builder()
                .user(user)
                .title(title)
                .startTime(startTime)
                .endTime(addThirtyMinutes(endTime))
                .build();
    }
}