package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.AdminUser;
import side.onetime.dto.adminUser.request.RegisterAdminUserRequest;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.AdminUserErrorStatus;
import side.onetime.repository.AdminUserRepository;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;

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
}
