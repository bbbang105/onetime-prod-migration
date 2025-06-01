package side.onetime.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
     * 클라이언트가 제공한 유효한 리프레쉬 토큰과 User-Agent를 기반으로
     * 브라우저를 식별하고, 해당 브라우저에 대한 토큰이 존재할 경우
     * 새로운 액세스 토큰과 리프레쉬 토큰을 발급하여 반환합니다.
     *
     * @param reissueAccessTokenRequest 클라이언트의 리프레쉬 토큰 요청 객체
     * @return 새로운 액세스 토큰과 리프레쉬 토큰을 포함한 응답 객체
     */
    @PostMapping("/action-reissue")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> reissueToken(
            @Valid @RequestBody ReissueTokenRequest reissueAccessTokenRequest,
            @Parameter(hidden = true) @RequestHeader("User-Agent") String userAgent) {

        ReissueTokenResponse reissueTokenResponse = tokenService.reissueToken(reissueAccessTokenRequest, jwtUtil.hashUserAgent(userAgent));
        return ApiResponse.onSuccess(SuccessStatus._REISSUE_TOKENS, reissueTokenResponse);
    }
}
