package side.onetime.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.admin.request.*;
import side.onetime.dto.admin.response.*;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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

        adminService.registerAdminUser(request);
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

        LoginAdminUserResponse response = adminService.loginAdminUser(request);
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

        GetAdminUserProfileResponse response = adminService.getAdminUserProfile(authorizationHeader);
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

        List<AdminUserDetailResponse> response = adminService.getAllAdminUserDetail(authorizationHeader);
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

        adminService.updateAdminUserStatus(authorizationHeader, request);
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

        adminService.withdrawAdminUser(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._WITHDRAW_ADMIN_USER);
    }

    /**
     * 대시보드 이벤트 목록 조회 API.
     *
     * 정렬 기준으로는 created_date, end_time, participant_count 등이 가능하며,
     * 응답은 최대 20개씩 페이지 단위로 제공됩니다.
     *
     * @param authorizationHeader Authorization 헤더 (Bearer 토큰)
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @param keyword 정렬 기준 필드명 (예: "created_date", "end_time", "participant_count")
     * @param sorting 정렬 방향 ("asc" 또는 "desc")
     * @return 이벤트 목록 및 페이지 정보가 포함된 응답 DTO
     */
    @GetMapping("/dashboard/events")
    public ResponseEntity<ApiResponse<GetAllDashboardEventsResponse>> getAllDashboardEvents(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "keyword", defaultValue = "created_date") String keyword,
            @RequestParam(value = "sorting", defaultValue = "desc") String sorting
    ) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        GetAllDashboardEventsResponse response = adminService.getAllDashboardEvents(authorizationHeader, pageable, keyword, sorting);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_DASHBOARD_EVENTS, response);
    }

    /**
     * 대시보드 사용자 목록 조회 API.
     *
     * 정렬 기준으로는 name, email, created_date, participation_count 등이 가능하며,
     * 응답은 최대 20개씩 페이지 단위로 제공됩니다.
     *
     * @param authorizationHeader Authorization 헤더 (Bearer 토큰)
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @param keyword 정렬 기준 필드명 (예: "name", "email", "created_date", "participation_count")
     * @param sorting 정렬 방향 ("asc" 또는 "desc")
     * @return 사용자 목록 및 페이지 정보가 포함된 응답 DTO
     */
    @GetMapping("/dashboard/users")
    public ResponseEntity<ApiResponse<GetAllDashboardUsersResponse>> getAllDashboardUsers(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "keyword", defaultValue = "created_date") String keyword,
            @RequestParam(value = "sorting", defaultValue = "desc") String sorting
    ) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        GetAllDashboardUsersResponse response = adminService.getAllDashboardUsers(authorizationHeader, pageable, keyword, sorting);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_DASHBOARD_USERS, response);
    }

    /**
     * 띠배너 등록 API.
     *
     * 요청으로 전달된 정보를 바탕으로 새로운 띠배너를 등록합니다.
     * 기본적으로 비활성화 상태이며 삭제되지 않은 상태로 생성됩니다.
     *
     * @param authorizationHeader 액세스 토큰
     * @param request 띠배너 등록 요청 정보
     * @return 성공 응답 메시지
     */
    @PostMapping("/banners/register")
    public ResponseEntity<ApiResponse<SuccessStatus>> registerBanner(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody RegisterBannerRequest request) {
        adminService.registerBanner(authorizationHeader, request);
        return ApiResponse.onSuccess(SuccessStatus._REGISTER_BANNER);
    }

    /**
     * 띠배너 단건 조회 API.
     *
     * 삭제되지 않은 배너 중, ID에 해당하는 배너를 조회합니다.
     *
     * @param authorizationHeader 액세스 토큰
     * @param id 조회할 배너 ID
     * @return 배너 응답 객체
     */
    @GetMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<GetBannerResponse>> getBanner(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id) {
        GetBannerResponse response = adminService.getBanner(authorizationHeader, id);
        return ApiResponse.onSuccess(SuccessStatus._GET_BANNER, response);
    }

    /**
     * 띠배너 전체 조회 API.
     *
     * 삭제되지 않은 모든 배너를 조회합니다.
     *
     * @param authorizationHeader 액세스 토큰
     * @return 배너 응답 객체 리스트
     */
    @GetMapping("/banners/all")
    public ResponseEntity<ApiResponse<List<GetBannerResponse>>> getAllBanners(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page
    ) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        List<GetBannerResponse> response = adminService.getAllBanners(authorizationHeader, pageable);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_BANNERS, response);
    }

    /**
     * 띠배너 수정 API.
     *
     * 일부 필드만 수정이 가능한 PATCH 방식의 API입니다.
     * 전달받은 요청에서 null이 아닌 필드만 수정되며,
     * 삭제된 배너는 수정할 수 없습니다.
     *
     * @param authorizationHeader 액세스 토큰
     * @param id 수정할 배너 ID
     * @param request 수정 요청 DTO
     * @return 성공 응답 메시지
     */
    @PatchMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateBanner(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBannerRequest request
    ) {
        adminService.updateBanner(authorizationHeader, id, request);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_BANNER);
    }

    /**
     * 띠배너 삭제 API.
     *
     * 배너를 DB에서 실제로 삭제하지 않고, isDeleted 플래그만 true로 변경합니다.
     * 해당 배너는 이후 조회되지 않으며 비활성화 상태로 간주됩니다.
     *
     * @param authorizationHeader 액세스 토큰
     * @param id 삭제할 배너 ID
     * @return 성공 응답 메시지
     */
    @DeleteMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<SuccessStatus>> deleteBanner(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id
    ) {
        adminService.deleteBanner(authorizationHeader, id);
        return ApiResponse.onSuccess(SuccessStatus._DELETE_BANNER);
    }
}
