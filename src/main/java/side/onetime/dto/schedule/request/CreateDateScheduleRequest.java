package side.onetime.dto.schedule.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import side.onetime.dto.schedule.response.DateSchedule;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateDateScheduleRequest(
        @NotBlank(message = "Event ID는 필수 값입니다.") String eventId,
        String memberId,
        @NotNull(message = "스케줄 목록은 필수 값입니다.") List<DateSchedule> dateSchedules
) {}