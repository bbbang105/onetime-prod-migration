package side.onetime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import side.onetime.domain.RefreshToken;
import side.onetime.dto.token.request.ReissueTokenRequest;
import side.onetime.dto.token.response.ReissueTokenResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.TokenErrorStatus;
import side.onetime.global.lock.annotation.DistributedLock;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.util.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 리프레시 토큰으로 액세스/리프레시 토큰을 재발행 하는 메서드.
     *
     * - 리프레시 토큰에서 userId, browserId 추출
     * - 동일 browserId에 대해 최근 요청 이력이 존재하면 쿨다운 예외 발생 (0.5초 제한)
     * - Redis에서 저장된 리프레시 토큰과 비교하여 유효성 검증
     * - 새로운 액세스/리프레시 토큰 발급 및 Redis에 저장
     * - 중복 재발행을 방지하기 위해 refreshToken 단위로 분산 락(@DistributedLock) 적용
     *
     * [예외 처리]
     * - 저장된 토큰이 없거나 일치하지 않으면 400 에러 반환
     * - 너무 자주 요청 시 429 에러 반환
     *
     * @param reissueTokenRequest 요청 객체 (리프레시 토큰 포함)
     * @return 새 액세스/리프레시 토큰
     * @throws CustomException 유효하지 않은 토큰이거나 요청이 너무 잦을 경우
     */
    @DistributedLock(prefix = "lock:reissue", key = "#reissueTokenRequest.refreshToken", waitTime = 0)
    public ReissueTokenResponse reissueToken(ReissueTokenRequest reissueTokenRequest) {
        String refreshToken = reissueTokenRequest.refreshToken();

        Long userId = jwtUtil.getClaimFromToken(refreshToken, "userId", Long.class);
        String browserId = jwtUtil.getClaimFromToken(refreshToken, "browserId", String.class);

        // 쿨다운 체크
        if (refreshTokenRepository.isInCooldown(userId, browserId)) {
            throw new CustomException(TokenErrorStatus._TOO_MANY_REQUESTS);
        }

        String existRefreshToken = refreshTokenRepository.findByUserIdAndBrowserId(userId, browserId)
                .orElseThrow(() -> new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN));

        if (!existRefreshToken.equals(refreshToken)) {
            throw new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, "USER");
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, browserId);
        refreshTokenRepository.save(new RefreshToken(userId, browserId, newRefreshToken));

        // 쿨다운 설정 (0.5초)
        refreshTokenRepository.setCooldown(userId, browserId, 500);

        return ReissueTokenResponse.of(newAccessToken, newRefreshToken);
    }
}
