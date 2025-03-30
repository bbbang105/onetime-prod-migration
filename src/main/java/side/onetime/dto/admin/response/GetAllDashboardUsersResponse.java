package side.onetime.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetAllDashboardUsersResponse(
        List<DashboardUser> users,
        PageInfo pageInfo
) {
    public static GetAllDashboardUsersResponse of(List<DashboardUser> users, PageInfo pageInfo) {
        return new GetAllDashboardUsersResponse(
                users,
                pageInfo
        );
    }
}