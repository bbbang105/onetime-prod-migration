package side.onetime.dto.admin.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GetAllDashboardEventsResponse(
        List<DashboardEvent> events,
        PageInfo pageInfo
) {
    public static GetAllDashboardEventsResponse of(List<DashboardEvent> events, PageInfo pageInfo) {
        return new GetAllDashboardEventsResponse(
                events,
                pageInfo
        );
    }
}
