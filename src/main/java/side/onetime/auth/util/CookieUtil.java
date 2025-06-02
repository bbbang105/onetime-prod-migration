package side.onetime.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.TokenErrorStatus;

public class CookieUtil {

    /**
     * 인증 쿠키를 설정합니다 (Refresh Token).
     *
     * @param response          HTTP 응답 객체
     * @param refreshToken      리프레시 토큰
     * @param refreshTokenExpiry 리프레시 토큰 만료 시간 (밀리초)
     */
    public static void setAuthCookies(HttpServletResponse response, String refreshToken, long refreshTokenExpiry) {
        setCookie(response, "refreshToken", refreshToken, (int) refreshTokenExpiry / 1000);
    }

    /**
     * HTTP-Only 쿠키를 설정합니다.
     *
     * @param response HTTP 응답 객체
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param maxAge   쿠키 만료 시간 (초)
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키에서 Refresh Token을 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 액세스 토큰
     * @throws CustomException 쿠키가 없거나 Refresh Token이 없는 경우
     */
    public static String getRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new CustomException(TokenErrorStatus._MISSING_REFRESH_TOKEN_IN_COOKIE);
    }
}
