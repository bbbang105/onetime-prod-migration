package side.onetime.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import side.onetime.domain.AdminUser;
import side.onetime.domain.User;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.AdminErrorStatus;
import side.onetime.exception.status.TokenErrorStatus;
import side.onetime.exception.status.UserErrorStatus;
import side.onetime.repository.AdminRepository;
import side.onetime.repository.UserRepository;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.access-token.expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME; // 액세스 토큰 유효기간

    @Value("${jwt.admin-user-access-token.expiration-time}")
    private long ADMIN_USER_ACCESS_TOKEN_EXPIRATION_TIME; // 어드민 유저 액세스 토큰 유효기간

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    @Value("${jwt.register-token.expiration-time}")
    private long REGISTER_TOKEN_EXPIRATION_TIME; // 레지스터 토큰 유효기간

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * JWT 서명 키를 생성 및 반환.
     *
     * @return SecretKey 객체
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 액세스 토큰 생성 메서드.
     *
     * @param userId 유저 ID
     * @param userType 유저 타입 (예: "USER" 또는 "ADMIN")
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(Long userId, String userType) {
        long expirationMillis;

        switch (userType.toUpperCase()) {
            case "ADMIN" -> {
                expirationMillis = ADMIN_USER_ACCESS_TOKEN_EXPIRATION_TIME;
            }
            case "USER" -> {
                expirationMillis = ACCESS_TOKEN_EXPIRATION_TIME;
            }
            default -> {
                log.warn("알 수 없는 타입의 액세스 토큰이 발행되었습니다. (userType: {})", userType);
                throw new CustomException(TokenErrorStatus._INVALID_USER_TYPE);
            }
        }

        return Jwts.builder()
                .claim("userId", userId)
                .claim("userType", userType.toUpperCase())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(this.getSigningKey())
                .compact();
    }

    /**
     * 레지스터 토큰 생성 메서드.
     *
     * @param provider 제공자
     * @param providerId 제공자 ID
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @return 생성된 레지스터 토큰
     */
    public String generateRegisterToken(String provider, String providerId, String name, String email) {
        return Jwts.builder()
                .claim("provider", provider)     // 클레임에 provider 추가
                .claim("providerId", providerId) // 클레임에 providerId 추가
                .claim("name", name)             // 클레임에 name 추가
                .claim("email", email)           // 클레임에 email 추가
                .claim("type", "REGISTER_TOKEN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REGISTER_TOKEN_EXPIRATION_TIME))
                .signWith(this.getSigningKey())
                .compact();
    }

    /**
     * 리프레시 토큰 생성 메서드.
     *
     * @param userId 유저 ID
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("type", "REFRESH_TOKEN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(this.getSigningKey())
                .compact();
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출.
     *
     * @param authorizationHeader Authorization 헤더
     * @return 토큰 문자열
     */
    public String getTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new CustomException(TokenErrorStatus._NOT_FOUND_HEADER);
        }
        return authorizationHeader.substring(7);
    }

    /**
     * 토큰에서 특정 클레임 값 추출.
     *
     * @param token JWT 토큰
     * @param key   클레임 키
     * @param clazz 반환할 값의 클래스 타입
     * @param <T>   반환할 값의 타입
     * @return 클레임 값
     */
    public <T> T getClaimFromToken(String token, String key, Class<T> clazz) {
        try {
            return Jwts.parser()
                    .verifyWith(this.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(key, clazz);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("토큰에서 '{}' claim 값을 추출하는 도중 에러가 발생했습니다: {}", key, e.getMessage());
            throw new CustomException(TokenErrorStatus._TOKEN_CLAIM_EXTRACTION_ERROR);
        }
    }

    /**
     * 헤더에서 유저 객체 반환.
     *
     * @param authorizationHeader Authorization 헤더
     * @return 유저 객체
     */
    public User getUserFromHeader(String authorizationHeader) {
        String token = getTokenFromHeader(authorizationHeader);
        validateToken(token);

        return userRepository.findById(getClaimFromToken(token, "userId", Long.class))
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
    }

    /**
     * 헤더에서 어드민 유저 객체 반환.
     *
     * @param authorizationHeader Authorization 헤더
     * @return 어드민 유저 객체
     */
    public AdminUser getAdminUserFromHeader(String authorizationHeader) {
        String token = getTokenFromHeader(authorizationHeader);
        validateToken(token);

        return adminRepository.findById(getClaimFromToken(token, "userId", Long.class))
                .orElseThrow(() -> new CustomException(AdminErrorStatus._NOT_FOUND_ADMIN_USER));
    }

    /**
     * JWT 토큰 검증.
     *
     * @param token JWT 토큰
     */
    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(this.getSigningKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
            throw new CustomException(TokenErrorStatus._TOKEN_SIGNATURE_INVALID);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            throw new CustomException(TokenErrorStatus._TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(TokenErrorStatus._TOKEN_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new CustomException(TokenErrorStatus._TOKEN_MALFORMED);
        }
    }
}
