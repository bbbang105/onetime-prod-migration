package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.UserDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.constant.SuccessStatus;
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
}