package side.onetime.dto.fixedEvent.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;
import side.onetime.dto.fixedEvent.response.FixedScheduleResponse;

import java.util.List;

import static side.onetime.util.DateUtil.addThirtyMinutes;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateFixedEventRequest(
        @NotBlank(message = "제목은 필수 값입니다.") String title,
        @NotNull(message = "스케줄 목록은 필수 값입니다.") List<FixedScheduleResponse> schedules
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