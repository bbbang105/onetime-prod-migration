package side.onetime.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import side.onetime.domain.RefreshToken;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    private final RedisTemplate redisTemplate;

    public void save(final RefreshToken refreshToken) {
        ValueOperations<Long, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getUserId(), refreshToken.getRefreshToken());
        redisTemplate.expire(refreshToken.getRefreshToken(), REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findByUserId(final Long userId) {
        ValueOperations<UUID, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(userId);

        if (Objects.isNull(refreshToken)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(userId, refreshToken));
    }
}