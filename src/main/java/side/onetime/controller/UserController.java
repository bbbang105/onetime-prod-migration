package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import side.onetime.auth.dto.CustomUserDetails;
import side.onetime.dto.user.request.OnboardUserRequest;
import side.onetime.dto.user.request.UpdateUserPolicyAgreementRequest;
import side.onetime.dto.user.request.UpdateUserProfileRequest;
import side.onetime.dto.user.request.UpdateUserSleepTimeRequest;
import side.onetime.dto.user.response.GetUserPolicyAgreementResponse;
import side.onetime.dto.user.response.GetUserProfileResponse;
import side.onetime.dto.user.response.GetUserSleepTimeResponse;
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
     * 유저 온보딩 API.
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
     * 유저 정보 조회 API.
     *
     * 로그인한 유저의 닉네임과 이메일 정보를 조회합니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @return 유저의 닉네임과 이메일을 포함한 응답 객체
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<GetUserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GetUserProfileResponse getUserProfileResponse = userService.getUserProfile(customUserDetails.user());
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_PROFILE, getUserProfileResponse);
    }

    /**
     * 유저 정보 수정 API.
     *
     * 유저의 닉네임을 수정하는 API입니다. 수정된 닉네임은 최대 길이 제한을 받습니다.
     *
     * @param customUserDetails        인증된 사용자 정보
     * @param updateUserProfileRequest 수정할 닉네임을 포함하는 요청 객체
     * @return 성공 상태 응답 객체
     */
    @PatchMapping("/profile/action-update")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {

        userService.updateUserProfile(customUserDetails.user(), updateUserProfileRequest);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_USER_PROFILE);
    }

    /**
     * 유저 서비스 탈퇴 API.
     *
     * 유저의 계정을 삭제하여 서비스에서 탈퇴하는 API입니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @return 성공 상태 응답 객체
     */
    @PostMapping("/action-withdraw")
    public ResponseEntity<ApiResponse<SuccessStatus>> withdrawService(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        userService.withdrawService(customUserDetails.user());
        return ApiResponse.onSuccess(SuccessStatus._WITHDRAW_SERVICE);
    }

    /**
     * 유저 약관 동의 여부 조회 API.
     *
     * 인증된 사용자의 필수 및 선택 약관 동의 상태를 조회합니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @return 약관 동의 여부 응답 객체
     */
    @GetMapping("/policy")
    public ResponseEntity<ApiResponse<GetUserPolicyAgreementResponse>> getUserPolicyAgreement(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GetUserPolicyAgreementResponse response = userService.getUserPolicyAgreement(customUserDetails.user());
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_POLICY_AGREEMENT, response);
    }

    /**
     * 유저 약관 동의 여부 수정 API.
     *
     * 사용자의 서비스 이용약관, 개인정보 수집 및 이용 동의, 마케팅 정보 수신 동의 상태를 업데이트합니다.
     * 모든 필드는 필수 값이며, 기존 동의 여부를 새로운 값으로 변경합니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @param request 약관 동의 여부 수정 요청 데이터 (필수 값)
     * @return 성공 상태 응답 객체
     */
    @PutMapping("/policy")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserPolicyAgreement(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateUserPolicyAgreementRequest request) {

        userService.updateUserPolicyAgreement(customUserDetails.user(), request);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_USER_POLICY_AGREEMENT);
    }

    /**
     * 유저 수면 시간 조회 API.
     *
     * 로그인한 사용자의 수면 시작 시간과 종료 시간을 조회합니다.
     *
     * @param customUserDetails 인증된 사용자 정보 (JWT 인증)
     * @return 유저의 수면 시작 시간 및 종료 시간을 포함한 응답 객체
     */
    @GetMapping("/sleep-time")
    public ResponseEntity<ApiResponse<GetUserSleepTimeResponse>> getUserSleepTime(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GetUserSleepTimeResponse response = userService.getUserSleepTime(customUserDetails.user());
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_SLEEP_TIME, response);
    }

    /**
     * 유저 수면 시간 수정 API.
     *
     * 사용자의 수면 시작 시간과 종료 시간을 업데이트합니다.
     *
     * @param customUserDetails 인증된 사용자 정보 (JWT 인증)
     * @param request 수면 시간 수정 요청 데이터 (필수 값)
     * @return 성공 상태 응답 객체
     */
    @PutMapping("/sleep-time")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserSleepTime(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateUserSleepTimeRequest request) {

        userService.updateUserSleepTime(customUserDetails.user(), request);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_USER_SLEEP_TIME);
    }
}
