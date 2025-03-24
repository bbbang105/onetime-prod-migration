package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.adminUser.request.LoginAdminUserRequest;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.dto.adminUser.request.UpdateAdminUserStatusRequest;
import side.onetime.dto.adminUser.response.AdminUserDetailResponse;
import side.onetime.dto.adminUser.response.GetAdminUserProfileResponse;
import side.onetime.dto.adminUser.response.LoginAdminUserResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.AdminUserService;

import java.util.List;

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
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<GetAdminUserProfileResponse>> getAdminUserProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        GetAdminUserProfileResponse response = adminUserService.getAdminUserProfile(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_ADMIN_USER_PROFILE, response);
    }

    /**
     * 전체 관리자 정보 조회 API.
     *
     * 요청 헤더에 포함된 액세스 토큰을 기반으로, 마스터 권한을 가진 관리자인 경우
     * 시스템에 등록된 모든 관리자 계정 정보를 조회하여 반환합니다.
     *
     * 마스터 관리자가 아닐 경우 예외가 발생하며, 유효한 경우 모든 관리자 이름, 이메일, 상태 정보가 포함됩니다.
     *
     * @param authorizationHeader Authorization 헤더에 포함된 액세스 토큰
     * @return 전체 관리자 프로필 목록이 포함된 응답 객체
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AdminUserDetailResponse>>> getAllAdminUserDetail(
            @RequestHeader("Authorization") String authorizationHeader) {

        List<AdminUserDetailResponse> response = adminUserService.getAllAdminUserDetail(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_ADMIN_USER_DETAIL, response);
    }

    /**
     * 관리자 권한 상태 수정 API.
     *
     * 마스터 관리자가 다른 관리자 계정의 권한 상태를 수정합니다.
     * 요청된 관리자 ID와 수정할 권한 상태를 바탕으로 권한을 변경하며,
     * 요청한 사용자가 마스터 관리자가 아닐 경우 예외가 발생합니다.
     *
     * @param authorizationHeader 요청자의 액세스 토큰
     * @param request 수정할 관리자 ID와 변경할 권한 상태를 담은 요청 객체
     * @return 성공 응답 메시지
     */
    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateAdminUserStatus(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UpdateAdminUserStatusRequest request) {

        adminUserService.updateAdminUserStatus(authorizationHeader, request);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_ADMIN_USER_STATUS);
    }

    /**
     * 관리자 계정 탈퇴 API.
     *
     * Authorization 헤더에 포함된 액세스 토큰을 통해 인증된 관리자 계정을 삭제합니다.
     * - 토큰에 포함된 ID로 관리자 정보를 조회하여 삭제합니다.
     *
     * @param authorizationHeader Authorization 헤더에 포함된 액세스 토큰
     * @return 성공 응답 메시지
     */
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<SuccessStatus>> withdrawAdminUser(
            @RequestHeader("Authorization") String authorizationHeader) {

        adminUserService.withdrawAdminUser(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._WITHDRAW_ADMIN_USER);
    }
}
