package side.onetime.dto.token.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReissueTokenResponse(
        String accessToken,
        String refreshToken
) {
    public static ReissueTokenResponse of(String accessToken, String refreshToken) {
        return new ReissueTokenResponse(accessToken, refreshToken);
    }
}