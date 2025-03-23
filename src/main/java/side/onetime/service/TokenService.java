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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 액세스 및 리프레쉬 토큰 재발행 메서드.
     *
     * 주어진 리프레쉬 토큰을 검증하고, 새로운 액세스 토큰과 리프레쉬 토큰을 생성 및 반환합니다.
     * 새로운 리프레쉬 토큰은 저장소에 저장되며, 기존 리프레쉬 토큰은 삭제됩니다.
     *
     * @param reissueTokenRequest 토큰 재발행 요청 데이터
     * @return 새로운 액세스 토큰 및 리프레쉬 토큰을 포함한 응답 데이터
     */
    public ReissueTokenResponse reissueToken(ReissueTokenRequest reissueTokenRequest) {
        String refreshToken = reissueTokenRequest.refreshToken();

        Long userId = jwtUtil.getClaimFromToken(refreshToken, "userId", Long.class);
        List<String> existRefreshTokens = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN));

        if (!existRefreshTokens.contains(refreshToken)) {
            // RefreshToken이 존재하지 않으면 예외 발생.
            throw new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN);
        }

        // 새로운 AccessToken 생성.
        String newAccessToken = jwtUtil.generateAccessToken(userId, "USER");

        // 새로운 RefreshToken 생성 및 저장.
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);
        refreshTokenRepository.save(new RefreshToken(userId, newRefreshToken));

        log.info("토큰 재발행에 성공하였습니다.");
        return ReissueTokenResponse.of(newAccessToken, newRefreshToken);
    }
}
