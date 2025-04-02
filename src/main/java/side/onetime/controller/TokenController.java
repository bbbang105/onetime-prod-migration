package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.token.request.ReissueTokenRequest;
import side.onetime.dto.token.response.ReissueTokenResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.TokenService;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    /**
     * 액세스 토큰 재발행 API.
     *
     * 이 API는 유효한 리프레쉬 토큰을 제공받아 새 액세스 토큰과 리프레쉬 토큰을 재발행합니다.
     * 리프레쉬 토큰의 유효성을 검증하고, 인증 정보에 따라 토큰을 갱신합니다.
     *
     * @param reissueAccessTokenRequest 리프레쉬 토큰을 포함한 요청 객체
     * @return 재발행된 액세스 토큰과 리프레쉬 토큰을 포함하는 응답 객체
     */
    @PostMapping("/action-reissue")
    public ResponseEntity<ApiResponse<ReissueTokenResponse>> reissueToken(
            @Valid @RequestBody ReissueTokenRequest reissueAccessTokenRequest) {

        ReissueTokenResponse reissueTokenResponse = tokenService.reissueToken(reissueAccessTokenRequest);
        return ApiResponse.onSuccess(SuccessStatus._REISSUE_TOKENS, reissueTokenResponse);
    }
}
