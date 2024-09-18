package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}