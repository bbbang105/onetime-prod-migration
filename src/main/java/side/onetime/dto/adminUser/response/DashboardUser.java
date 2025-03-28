package side.onetime.dto.adminUser.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.User;
import side.onetime.domain.enums.Language;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DashboardUser(
        Long id,
        String name,
        String email,
        String nickname,
        String provider,
        String providerId,
        Boolean servicePolicyAgreement,
        Boolean privacyPolicyAgreement,
        Boolean marketingPolicyAgreement,
        String sleepStartTime,
        String sleepEndTime,
        Language language,
        int participationCount
) {
    public static DashboardUser from(User user, int participationCount) {
        return new DashboardUser(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getProvider(),
                user.getProviderId(),
                user.getServicePolicyAgreement(),
                user.getPrivacyPolicyAgreement(),
                user.getMarketingPolicyAgreement(),
                user.getSleepStartTime(),
                user.getSleepEndTime(),
                user.getLanguage(),
                participationCount
        );
    }
}
