package side.onetime.auth.dto;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiration,
        long refreshTokenExpiration,
        String domain
) {
    public static AuthTokenResponse of(
            String accessToken,
            String refreshToken,
            long accessTokenExpiration,
            long refreshTokenExpiration,
            String domain
    ) {
        return new AuthTokenResponse(accessToken, refreshToken, accessTokenExpiration, refreshTokenExpiration, domain);
    }
}
