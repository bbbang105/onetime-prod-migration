package side.onetime.auth.dto;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {
    public static AuthTokenResponse of(
            String accessToken,
            String refreshToken,
            long accessTokenExpiration,
            long refreshTokenExpiration
    ) {
        return new AuthTokenResponse(accessToken, refreshToken, accessTokenExpiration, refreshTokenExpiration);
    }
}
