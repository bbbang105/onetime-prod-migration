package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.UserDto;
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
    public ResponseEntity<ApiResponse<UserDto.OnboardUserResponse>> onboardUser(
            @RequestBody UserDto.OnboardUserRequest onboardUserRequest) {

        UserDto.OnboardUserResponse onboardUserResponse = userService.onboardUser(onboardUserRequest);
        return ApiResponse.onSuccess(SuccessStatus._ONBOARD_USER, onboardUserResponse);
    }

    // 유저 정보 조회 API
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto.GetUserProfileResponse>> getUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        UserDto.GetUserProfileResponse getUserProfileResponse = userService.getUserProfile(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_PROFILE, getUserProfileResponse);
    }

    // 유저 정보 수정 API
    @PatchMapping("/profile/action-update")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDto.UpdateUserProfileRequest updateUserProfileRequest) {

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