package side.onetime.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import side.onetime.auth.dto.AuthTokenResponse;
import side.onetime.auth.util.CookieUtil;
import side.onetime.domain.RefreshToken;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.TokenErrorStatus;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.util.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.access-token.expiration-time}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-time}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 리프레시 토큰을 이용한 토큰 재발행 메서드.
     *
     * @param browserId User-Agent 기반 해시 브라우저 식별자
     * @return 새로운 액세스/리프레시 토큰과 만료 시간을 담은 응답 객체
     * @throws CustomException 유효하지 않거나 저장된 리프레시 토큰이 없을 경우
     */
    public AuthTokenResponse reissueToken(HttpServletRequest request, String browserId) {
        String refreshToken = CookieUtil.getRefreshTokenFromCookies(request);

        Long userId = jwtUtil.getClaimFromToken(refreshToken, "userId", Long.class);
        String existRefreshToken = refreshTokenRepository.findByUserIdAndBrowserId(userId, browserId)
                .orElseThrow(() -> new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN));

        if (!existRefreshToken.equals(refreshToken)) {
            throw new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, "USER");
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        refreshTokenRepository.save(new RefreshToken(userId, browserId, newRefreshToken));

        return AuthTokenResponse.of(newAccessToken, newRefreshToken, accessTokenExpiration, refreshTokenExpiration);
    }
}
