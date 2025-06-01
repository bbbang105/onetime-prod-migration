package side.onetime.domain;

import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class RefreshToken {
    @Id
    private Long userId;
    private String browserId;
    private String refreshToken;

    public RefreshToken(Long userId, String browserId, String refreshToken) {
        this.userId = userId;
        this.browserId = browserId;
        this.refreshToken = refreshToken;
    }
}
