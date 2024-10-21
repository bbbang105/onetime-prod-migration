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

    // 유저 온보딩 API
    @PostMapping("/onboarding")
    public ResponseEntity<ApiResponse<OnboardUserResponse>> onboardUser(
            @Valid @RequestBody OnboardUserRequest onboardUserRequest) {

        OnboardUserResponse onboardUserResponse = userService.onboardUser(onboardUserRequest);
        return ApiResponse.onSuccess(SuccessStatus._ONBOARD_USER, onboardUserResponse);
    }

    // 유저 정보 조회 API
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<GetUserProfileResponse>> getUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        GetUserProfileResponse getUserProfileResponse = userService.getUserProfile(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_PROFILE, getUserProfileResponse);
    }

    // 유저 정보 수정 API
    @PatchMapping("/profile/action-update")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {

        userService.updateUserProfile(authorizationHeader, updateUserProfileRequest);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_USER_PROFILE);
    }

    // 유저 서비스 탈퇴 API
    @PostMapping("/action-withdraw")
    public ResponseEntity<ApiResponse<SuccessStatus>> withdrawService(
            @RequestHeader("Authorization") String authorizationHeader) {

        userService.withdrawService(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._WITHDRAW_SERVICE);
    }
}