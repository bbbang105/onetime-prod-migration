package side.onetime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    // 액세스 & 리프레쉬 토큰 재발행 메서드
    public ReissueTokenResponse reissueToken(ReissueTokenRequest reissueTokenRequest) {
        String refreshToken = reissueTokenRequest.refreshToken();

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        List<String> existRefreshTokens = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN));

        if (!existRefreshTokens.contains(refreshToken)) {
            // RefreshToken이 존재하지 않으면 예외 발생
            throw new CustomException(TokenErrorStatus._NOT_FOUND_REFRESH_TOKEN);
        }

        // 새로운 AccessToken 생성
        String newAccessToken = jwtUtil.generateAccessToken(userId, ACCESS_TOKEN_EXPIRATION_TIME);

        // 새로운 RefreshToken 생성 및 저장
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, REFRESH_TOKEN_EXPIRATION_TIME);
        refreshTokenRepository.save(new RefreshToken(userId, newRefreshToken));

        log.info("토큰 재발행에 성공하였습니다.");
        return ReissueTokenResponse.of(newAccessToken, newRefreshToken);
    }
}