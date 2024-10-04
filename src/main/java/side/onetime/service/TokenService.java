package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import side.onetime.domain.RefreshToken;
import side.onetime.dto.TokenDto;
import side.onetime.exception.TokenErrorResult;
import side.onetime.exception.TokenException;
import side.onetime.repository.RefreshTokenRepository;
import side.onetime.util.JwtUtil;

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
    public TokenDto.ReissueTokenResponse reissueToken(TokenDto.ReissueTokenRequest reissueTokenRequest) {
        String refreshToken = reissueTokenRequest.getRefreshToken();
        jwtUtil.validateTokenExpiration(refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new TokenException(TokenErrorResult._NOT_FOUND_REFRESH_TOKEN));
        String newAccessToken;

        if (!existRefreshToken.getRefreshToken().equals(refreshToken)) {
            // 리프레쉬 토큰이 다른 경우
            throw new TokenException(TokenErrorResult._INVALID_REFRESH_TOKEN); // 401 에러를 던져 재로그인을 요청
        } else {
            // 액세스 토큰 재발급
            newAccessToken = jwtUtil.generateAccessToken(userId, ACCESS_TOKEN_EXPIRATION_TIME);
        }

        // 새로운 리프레쉬 토큰 Redis 저장
        RefreshToken newRefreshToken = new RefreshToken(userId, jwtUtil.generateRefreshToken(userId, REFRESH_TOKEN_EXPIRATION_TIME));
        refreshTokenRepository.save(newRefreshToken);

        return TokenDto.ReissueTokenResponse.of(newAccessToken, newRefreshToken.getRefreshToken());
    }
}