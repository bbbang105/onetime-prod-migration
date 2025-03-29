package side.onetime.dto.admin.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import side.onetime.domain.enums.AdminStatus;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateAdminUserStatusRequest(
        @NotNull(message = "관리자 계정 아이디는 필수 값입니다.")
        Long id,

        @NotNull(message = "변경할 관리자 상태는 필수 값입니다.")
        AdminStatus adminStatus
) {
}
