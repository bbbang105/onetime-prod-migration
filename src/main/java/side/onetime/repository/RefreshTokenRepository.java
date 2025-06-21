package side.onetime.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import side.onetime.domain.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private static final int REFRESH_TOKEN_LIMIT = 5;
    private static final String COOLDOWN_PREFIX = "cooldown:reissue:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(RefreshToken refreshToken) {
        String key = "refreshToken:" + refreshToken.getUserId();
        String value = refreshToken.getBrowserId() + ":" + refreshToken.getRefreshToken();

        List<String> existing = redisTemplate.opsForList().range(key, 0, -1);

        if (existing != null) {
            // 기존 토큰 제거
            existing.removeIf(token -> token.startsWith(refreshToken.getBrowserId() + ":"));
            redisTemplate.delete(key);
            for (String item : existing) {
                redisTemplate.opsForList().rightPush(key, item);
            }
        }

        // 최신 토큰 맨 앞에 추가
        redisTemplate.opsForList().leftPush(key, value);
        redisTemplate.opsForList().trim(key, 0, REFRESH_TOKEN_LIMIT - 1);
        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    public Optional<String> findByUserIdAndBrowserId(Long userId, String browserId) {
        String key = "refreshToken:" + userId;
        List<String> tokens = redisTemplate.opsForList().range(key, 0, -1);

        if (tokens == null) return Optional.empty();

        return tokens.stream()
                .filter(t -> t.startsWith(browserId + ":"))
                .findFirst()
                .map(t -> t.substring(browserId.length() + 1));
    }

    public boolean isInCooldown(Long userId, String browserId) {
        String key = COOLDOWN_PREFIX + userId + ":" + browserId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setCooldown(Long userId, String browserId, long millis) {
        String key = COOLDOWN_PREFIX + userId + ":" + browserId;
        redisTemplate.opsForValue().set(key, "1", millis, TimeUnit.MILLISECONDS);
    }

    public void deleteAllByUserId(Long userId) {
        String pattern = "refreshToken:" + userId;
        redisTemplate.delete(pattern);
    }

    public void deleteRefreshToken(Long userId, String browserId) {
        String key = "refreshToken:" + userId;

        List<String> tokens = redisTemplate.opsForList().range(key, 0, -1);
        if (tokens == null || tokens.isEmpty()) return;

        tokens.removeIf(token -> token.startsWith(browserId + ":"));

        redisTemplate.delete(key);
        for (String token : tokens) {
            redisTemplate.opsForList().rightPush(key, token);
        }

        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }
}
