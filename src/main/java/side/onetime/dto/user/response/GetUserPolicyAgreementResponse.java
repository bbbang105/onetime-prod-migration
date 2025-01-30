package side.onetime.dto.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetUserPolicyAgreementResponse(
        Boolean essentialPolicyAgreement,
        Boolean optionalPolicyAgreement
) {
    public static GetUserPolicyAgreementResponse from(User user) {
        return new GetUserPolicyAgreementResponse(
                user.getEssentialPolicyAgreement() != null ? user.getEssentialPolicyAgreement() : false,
                user.getOptionalPolicyAgreement() != null ? user.getOptionalPolicyAgreement() : false
        );
    }
}
