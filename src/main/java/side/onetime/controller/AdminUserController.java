package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.AdminUserService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    /**
     * 관리자 회원가입 API.
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
}
