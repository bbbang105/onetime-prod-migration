package side.onetime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import side.onetime.auth.dto.AuthTokenResponse;
import side.onetime.domain.RefreshToken;
import side.onetime.dto.token.request.ReissueTokenRequest;
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
     * 서버가 쿠키에서 추출한 리프레시 토큰을 기반으로 유효성을 검사하고,
     * 해당 브라우저 ID에 대한 저장된 토큰과 일치하는 경우,
     * 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     * 이후 기존 토큰은 삭제되고 최신 토큰으로 갱신됩니다.
     *
     * @param reissueTokenRequest 리프레시 토큰 요청 객체
     * @param browserId User-Agent 기반 해시 브라우저 식별자
     * @return 새롭게 발급된 액세스 토큰 및 리프레시 토큰을 포함한 응답 객체
     * @throws CustomException 토큰이 유효하지 않거나 존재하지 않을 경우 예외 발생
     */
    public AuthTokenResponse reissueToken(ReissueTokenRequest reissueTokenRequest, String browserId) {
        String refreshToken = reissueTokenRequest.refreshToken();

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
