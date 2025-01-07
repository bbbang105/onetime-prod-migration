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
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간.

    private static final int REFRESH_TOKEN_LIMIT = 5; // 최대 5개로 제한.

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * RefreshToken 저장 메서드.
     *
     * 유저 ID를 키로 하여 Redis에 RefreshToken 리스트를 저장합니다.
     * 새로 추가된 토큰을 리스트의 맨 앞에 배치하며, 토큰 수를 최대 5개로 제한합니다.
     * 저장된 키의 만료 시간은 리프레쉬 토큰의 유효기간에 맞춰 설정됩니다.
     *
     * @param refreshToken 저장할 RefreshToken 객체
     */
    public void save(final RefreshToken refreshToken) {
        String key = "refreshToken:" + refreshToken.getUserId();
        redisTemplate.opsForList().leftPush(key, refreshToken.getRefreshToken());
        redisTemplate.opsForList().trim(key, 0, REFRESH_TOKEN_LIMIT - 1);
        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 유저 ID로 RefreshToken 리스트 조회 메서드.
     *
     * 유저 ID를 기반으로 Redis에 저장된 RefreshToken 리스트를 조회합니다.
     * 리스트가 없거나 비어 있을 경우 Optional.empty()를 반환합니다.
     *
     * @param userId 조회할 유저 ID
     * @return RefreshToken 리스트를 포함하는 Optional 객체
     */
    public Optional<List<String>> findByUserId(final Long userId) {
        String key = "refreshToken:" + userId;
        List<String> refreshTokens = redisTemplate.opsForList().range(key, 0, -1);

        if (Objects.isNull(refreshTokens) || refreshTokens.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(refreshTokens);
    }
}
