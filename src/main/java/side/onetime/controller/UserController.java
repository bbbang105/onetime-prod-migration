package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.user.request.OnboardUserRequest;
import side.onetime.dto.user.request.UpdateUserProfileRequest;
import side.onetime.dto.user.response.GetUserProfileResponse;
import side.onetime.dto.user.response.OnboardUserResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저 온보딩 API
     *
     * 회원가입 이후, 유저의 닉네임을 설정하고 온보딩을 완료하는 API입니다.
     * 주어진 레지스터 토큰을 통해 사용자 정보를 확인한 후, 액세스 토큰과 리프레쉬 토큰을 발급하여 반환합니다.
     *
     * @param onboardUserRequest 유저의 레지스터 토큰과 닉네임 정보를 포함하는 요청 객체
     * @return 발급된 액세스 토큰과 리프레쉬 토큰을 포함하는 응답 객체
     */
    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<OnboardUserResponse>> onboardUser(
            @Valid @RequestBody OnboardUserRequest onboardUserRequest) {

        OnboardUserResponse onboardUserResponse = userService.onboardUser(onboardUserRequest);
        return ApiResponse.onSuccess(SuccessStatus._ONBOARD_USER, onboardUserResponse);
    }

    /**
     * 유저 정보 조회 API
     *
     * 로그인한 유저의 닉네임과 이메일 정보를 조회합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @return 유저의 닉네임과 이메일을 포함한 응답 객체
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<GetUserProfileResponse>> getUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        GetUserProfileResponse getUserProfileResponse = userService.getUserProfile(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_PROFILE, getUserProfileResponse);
    }

    /**
     * 유저 정보 수정 API
     *
     * 유저의 닉네임을 수정하는 API입니다. 수정된 닉네임은 최대 길이 제한을 받습니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param updateUserProfileRequest 수정할 닉네임을 포함하는 요청 객체
     * @return 성공 상태 응답 객체
     */
    @PatchMapping("/profile/action-update")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {

        userService.updateUserProfile(authorizationHeader, updateUserProfileRequest);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_USER_PROFILE);
    }

    /**
     * 유저 서비스 탈퇴 API
     *
     * 유저의 계정을 삭제하여 서비스에서 탈퇴하는 API입니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @return 성공 상태 응답 객체
     */
    @PostMapping("/action-withdraw")
    public ResponseEntity<ApiResponse<SuccessStatus>> withdrawService(
            @RequestHeader("Authorization") String authorizationHeader) {

        userService.withdrawService(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._WITHDRAW_SERVICE);
    }
}