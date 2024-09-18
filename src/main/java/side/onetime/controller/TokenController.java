package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.TokenDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.constant.SuccessStatus;
import side.onetime.service.TokenService;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    // 액세스 토큰 재발행 API
    @PostMapping("/action-reissue")
    public ResponseEntity<ApiResponse<TokenDto.ReissueTokenResponse>> reissueToken(
            @RequestBody TokenDto.ReissueTokenRequest reissueAccessTokenRequest) {

        TokenDto.ReissueTokenResponse reissueTokenResponse = tokenService.reissueToken(reissueAccessTokenRequest);
        return ApiResponse.onSuccess(SuccessStatus._REISSUE_TOKENS, reissueTokenResponse);
    }
}