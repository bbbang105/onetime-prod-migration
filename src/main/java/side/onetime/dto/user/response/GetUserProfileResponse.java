package side.onetime.dto.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.User;
import side.onetime.domain.enums.Language;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetUserProfileResponse(
        String nickname,
        String email,
        Language language,
        String socialPlatform
) {
    public static GetUserProfileResponse of(User user) {
        return new GetUserProfileResponse(
                user.getNickname(),
                user.getEmail(),
                user.getLanguage(),
                user.getProvider()
        );
    }
}
