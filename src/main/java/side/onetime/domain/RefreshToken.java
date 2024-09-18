package side.onetime.domain;

import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class RefreshToken {
    @Id
    private Long userId;
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}