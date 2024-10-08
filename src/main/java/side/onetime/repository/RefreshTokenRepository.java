package side.onetime.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import side.onetime.domain.RefreshToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    private static final int REFRESH_TOKEN_LIMIT = 5; // 최대 5개로 제한

    private final RedisTemplate<String, String> redisTemplate;

    // RefreshToken 리스트에 새로운 토큰을 추가
    public void save(final RefreshToken refreshToken) {
        String key = "refreshToken:" + refreshToken.getUserId();
        // 맨 앞에 추가
        redisTemplate.opsForList().leftPush(key, refreshToken.getRefreshToken());

        // 가장 오래된 리프레쉬 토큰을 삭제
        redisTemplate.opsForList().trim(key, 0, REFRESH_TOKEN_LIMIT - 1);

        // 만료 시간 설정 (전체 리스트의 키에 적용)
        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    // 유저 ID로 RefreshToken 리스트 조회
    public Optional<List<String>> findByUserId(final Long userId) {
        String key = "refreshToken:" + userId;

        List<String> refreshTokens = redisTemplate.opsForList().range(key, 0, -1);

        if (Objects.isNull(refreshTokens) || refreshTokens.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(refreshTokens);
    }
}
