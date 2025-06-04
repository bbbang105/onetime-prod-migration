package side.onetime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import side.onetime.domain.RefreshToken;
import side.onetime.dto.token.request.ReissueTokenRequest;
import side.onetime.dto.token.response.ReissueTokenResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.TokenErrorStatus;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.util.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 리프레시 토큰을 이용한 토큰 재발행 메서드.
     *
     * 주어진 리프레시 토큰의 유효성을 확인하고, 일치하는 브라우저 ID에 대해
     * 새로운 액세스 토큰 및 리프레시 토큰을 발급합니다.
     * 기존 토큰은 삭제되고 최신 토큰으로 갱신됩니다.
     *
     * @param reissueTokenRequest 클라이언트가 보낸 리프레시 토큰 요청 객체
     * @return 새롭게 발급된 액세스 토큰 및 리프레시 토큰 응답 객체
     * @throws CustomException 유효하지 않은 리프레시 토큰일 경우 예외 발생
     */
    public ReissueTokenResponse reissueToken(ReissueTokenRequest reissueTokenRequest) {
        String refreshToken = reissueTokenRequest.refreshToken();

        Long userId = jwtUtil.getClaimFromToken(refreshToken, "userId", Long.class);
        String browserId = jwtUtil.getClaimFromToken(refreshToken, "browserId", String.class);
        String existRefreshToken = refreshTokenRepository.findByUserIdAndBrowserId(userId, browserId)
                .orElseThrow(() -> new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN));

        if (!existRefreshToken.equals(refreshToken)) {
            throw new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, "USER");
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, browserId);
        refreshTokenRepository.save(new RefreshToken(userId, browserId, newRefreshToken));

        return ReissueTokenResponse.of(newAccessToken, newRefreshToken);
    }
}
