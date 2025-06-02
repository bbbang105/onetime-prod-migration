package side.onetime.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.auth.dto.AuthTokenResponse;
import side.onetime.auth.util.CookieUtil;
import side.onetime.dto.token.request.ReissueTokenRequest;
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
     * 클라이언트의 요청 시, 서버가 쿠키에서 리프레쉬 토큰을 추출하고
     * User-Agent 기반 브라우저 식별자를 활용하여 해당 브라우저에 대한 토큰이 유효한지 확인한 뒤,
     * 새로운 액세스 토큰과 리프레쉬 토큰을 재발급하여 반환합니다.
     *
     * @param reissueAccessTokenRequest 클라이언트 요청 객체
     * @param userAgent 클라이언트의 User-Agent 헤더
     * @param response 재발급된 리프레쉬 토큰을 쿠키로 설정하는 데 사용
     * @return 새로운 액세스 토큰 정보를 포함한 응답
     */
    @PostMapping("/action-reissue")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> reissueToken(
            @Valid @RequestBody ReissueTokenRequest reissueAccessTokenRequest,
            @Parameter(hidden = true) @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response) {

        AuthTokenResponse authTokenResponse = tokenService.reissueToken(reissueAccessTokenRequest, jwtUtil.hashUserAgent(userAgent));
        CookieUtil.setAuthCookies(response, authTokenResponse.refreshToken(), authTokenResponse.refreshTokenExpiration());
        return ApiResponse.onSuccess(SuccessStatus._REISSUE_TOKENS, ReissueTokenResponse.of(authTokenResponse.accessToken()));
    }
}
