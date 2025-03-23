package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.AdminUser;
import side.onetime.domain.enums.AdminStatus;
import side.onetime.dto.adminUser.request.LoginAdminUserRequest;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.dto.adminUser.response.GetAdminUserProfileResponse;
import side.onetime.dto.adminUser.response.LoginAdminUserResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.AdminUserErrorStatus;
import side.onetime.repository.AdminUserRepository;
import side.onetime.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final JwtUtil jwtUtil;

    /**
     * 관리자 계정 등록 메서드.
     *
     * 입력된 이름, 이메일, 비밀번호 정보를 바탕으로 새로운 관리자 계정을 생성합니다.
     * 생성된 계정은 기본적으로 승인 대기 상태(PENDING_APPROVAL)로 저장됩니다.
     *
     * @param request 관리자 이름, 이메일, 비밀번호 정보를 담은 요청 객체
     */
    @Transactional
    public void registerAdminUser(RegisterAdminUserRequest request) {
        if (adminUserRepository.existsAdminUsersByEmail(request.email())) {
            throw new CustomException(AdminUserErrorStatus._IS_DUPLICATED_EMAIL);
        }
        AdminUser newAdminUser = request.toEntity();
        adminUserRepository.save(newAdminUser);
    }

    /**
     * 관리자 계정 로그인 메서드.
     *
     * 입력된 이메일과 비밀번호를 기반으로 관리자 계정 로그인을 시도합니다.
     * - 이메일이 존재하지 않으면 예외가 발생합니다.
     * - 계정이 승인 대기 상태인 경우 예외가 발생합니다.
     * - 비밀번호가 일치하지 않으면 예외가 발생합니다.
     *
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     */
    public LoginAdminUserResponse loginAdminUser(LoginAdminUserRequest request) {
        AdminUser adminUser = adminUserRepository.findAdminUserByEmail(request.email())
                        .orElseThrow(() -> new CustomException(AdminUserErrorStatus._NOT_FOUND_ADMIN_USER));
        if (AdminStatus.PENDING_APPROVAL == adminUser.getAdminStatus()) {
            throw new CustomException(AdminUserErrorStatus._IS_NOT_APPROVED_ADMIN_USER);
        }
        if (!request.password().equals(adminUser.getPassword())) {
            throw new CustomException(AdminUserErrorStatus._IS_NOT_EQUAL_PASSWORD);
        }
        return LoginAdminUserResponse.of(jwtUtil.generateAccessToken(adminUser.getId(), "ADMIN"));
    }

    /**
     * 관리자 프로필 조회 메서드.
     *
     * Authorization 헤더에서 액세스 토큰을 추출하고, 토큰에 포함된 ID를 기반으로
     * 관리자 정보를 조회합니다.
     * - 토큰이 유효하지 않거나 관리자 정보가 존재하지 않을 경우 예외가 발생합니다.
     *
     * @param authorizationHeader Authorization 헤더에 포함된 액세스 토큰
     * @return 관리자 프로필 응답 객체
     */
    public GetAdminUserProfileResponse getAdminUserProfile(String authorizationHeader) {
        AdminUser adminUser = jwtUtil.getAdminUserFromHeader(authorizationHeader);
        return GetAdminUserProfileResponse.from(adminUser);
    }
}
