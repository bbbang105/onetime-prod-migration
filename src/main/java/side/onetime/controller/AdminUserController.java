package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.adminUser.request.LoginAdminUserRequest;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.dto.adminUser.response.GetAdminUserProfileResponse;
import side.onetime.dto.adminUser.response.LoginAdminUserResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.AdminUserService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    /**
     * 관리자 계정 회원가입 API.
     *
     * 입력된 정보를 검증한 후, 새로운 관리자 계정을 생성합니다.
     * 생성된 계정은 승인 대기 상태로 등록되며, 관리자 승인 이후에만 접근 권한이 활성화됩니다.
     *
     * @param request 관리자 이름, 이메일, 비밀번호 정보를 담은 요청 객체
     * @return 성공 응답 메시지
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SuccessStatus>> registerAdminUser(
            @Valid @RequestBody RegisterAdminUserRequest request) {

        adminUserService.registerAdminUser(request);
        return ApiResponse.onSuccess(SuccessStatus._REGISTER_ADMIN_USER);
    }

    /**
     * 관리자 로그인 API.
     *
     * 입력된 정보를 검증한 후, 새로운 관리자 계정을 생성합니다.
     * 생성된 계정은 승인 대기 상태로 등록되며, 관리자 승인 이후에만 접근 권한이 활성화됩니다.
     *
     * @param request 관리자 이름, 이메일, 비밀번호 정보를 담은 요청 객체
     * @return 성공 응답 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginAdminUserResponse>> loginAdminUser(
            @Valid @RequestBody LoginAdminUserRequest request) {

        LoginAdminUserResponse response = adminUserService.loginAdminUser(request);
        return ApiResponse.onSuccess(SuccessStatus._LOGIN_ADMIN_USER, response);
    }

    /**
     * 관리자 프로필 조회 API.
     *
     * 요청 헤더에 포함된 액세스 토큰을 기반으로 로그인된 관리자 정보를 조회합니다.
     * 유효한 토큰이 아닐 경우 예외가 발생하며, 유효한 경우 이름, 이메일 정보를 반환합니다.
     *
     * @param authorizationHeader Authorization 헤더에 포함된 액세스 토큰
     * @return 관리자 프로필 정보가 포함된 응답 객체
     */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<GetAdminUserProfileResponse>> getAdminUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        GetAdminUserProfileResponse response = adminUserService.getAdminUserProfile(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_ADMIN_USER_PROFILE, response);
    }
}
