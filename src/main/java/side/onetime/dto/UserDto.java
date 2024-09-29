package side.onetime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.domain.User;

public class UserDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OnboardUserRequest {
        private String registerToken;
        private String nickname;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OnboardUserResponse {
        private String accessToken;
        private String refreshToken;

        public static OnboardUserResponse of(String accessToken, String refreshToken) {
            return OnboardUserResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetUserProfileResponse {
        private String nickname;
        private String email;

        public static GetUserProfileResponse of(User user) {
            return GetUserProfileResponse.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpdateUserProfileRequest {
        private String nickname;
    }
}