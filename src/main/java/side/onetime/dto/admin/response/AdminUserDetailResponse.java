package side.onetime.dto.admin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.AdminUser;
import side.onetime.domain.enums.AdminStatus;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdminUserDetailResponse(
        Long id,
        String name,
        String email,
        AdminStatus adminStatus
) {
    public static AdminUserDetailResponse from(AdminUser adminUser) {
        return new AdminUserDetailResponse(
                adminUser.getId(),
                adminUser.getName(),
                adminUser.getEmail(),
                adminUser.getAdminStatus()
        );
    }
}
