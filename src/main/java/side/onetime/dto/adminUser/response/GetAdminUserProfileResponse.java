package side.onetime.dto.adminUser.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.AdminUser;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetAdminUserProfileResponse(
        String name,
        String email
) {
    public static GetAdminUserProfileResponse from(AdminUser adminUser) {
        return new GetAdminUserProfileResponse(adminUser.getName(), adminUser.getEmail());
    }
}
