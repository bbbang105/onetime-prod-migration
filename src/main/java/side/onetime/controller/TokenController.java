package side.onetime.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.auth.dto.AuthTokenResponse;
import side.onetime.auth.util.CookieUtil;
import side.onetime.dto.token.response.ReissueTokenResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.TokenService;
import side.onetime.util.JwtUtil;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    /**
     * 액세스 토큰 재발행 API.
     *
     * @param userAgent 클라이언트의 User-Agent 헤더
     * @param request 클라이언트의 HTTP 요청
     * @param response 클라이언트의 HTTP 응답
     * @return 새로운 액세스 토큰을 포함한 응답 객체
     */
    @PostMapping("/action-reissue")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> reissueToken(
            @Parameter(hidden = true) @RequestHeader("User-Agent") String userAgent,
            HttpServletRequest request,
            HttpServletResponse response) {

        AuthTokenResponse authTokenResponse = tokenService.reissueToken(request, jwtUtil.hashUserAgent(userAgent));
        CookieUtil.setAuthCookies(response, authTokenResponse.refreshToken(), authTokenResponse.refreshTokenExpiration(), authTokenResponse.domain());
        return ApiResponse.onSuccess(SuccessStatus._REISSUE_TOKENS, ReissueTokenResponse.of(authTokenResponse.accessToken()));
    }
}
