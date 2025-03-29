package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.*;
import side.onetime.domain.enums.AdminStatus;
import side.onetime.domain.enums.EventStatus;
import side.onetime.dto.admin.request.LoginAdminUserRequest;
import side.onetime.dto.admin.request.RegisterAdminUserRequest;
import side.onetime.dto.admin.request.UpdateAdminUserStatusRequest;
import side.onetime.dto.admin.response.*;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.AdminErrorStatus;
import side.onetime.repository.*;
import side.onetime.util.JwtUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
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
        if (adminRepository.existsAdminUsersByEmail(request.email())) {
            throw new CustomException(AdminErrorStatus._IS_DUPLICATED_EMAIL);
        }
        AdminUser newAdminUser = request.toEntity();
        adminRepository.save(newAdminUser);
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
    @Transactional(readOnly = true)
    public LoginAdminUserResponse loginAdminUser(LoginAdminUserRequest request) {
        AdminUser adminUser = adminRepository.findAdminUserByEmail(request.email())
                        .orElseThrow(() -> new CustomException(AdminErrorStatus._NOT_FOUND_ADMIN_USER));
        if (AdminStatus.PENDING_APPROVAL == adminUser.getAdminStatus()) {
            throw new CustomException(AdminErrorStatus._IS_NOT_APPROVED_ADMIN_USER);
        }
        if (!request.password().equals(adminUser.getPassword())) {
            throw new CustomException(AdminErrorStatus._IS_NOT_EQUAL_PASSWORD);
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
    @Transactional(readOnly = true)
    public GetAdminUserProfileResponse getAdminUserProfile(String authorizationHeader) {
        AdminUser adminUser = jwtUtil.getAdminUserFromHeader(authorizationHeader);
        return GetAdminUserProfileResponse.from(adminUser);
    }

    /**
     * 전체 관리자 정보 조회 메서드.
     *
     * Authorization 헤더에서 액세스 토큰을 추출하고, 해당 토큰의 소유자가 마스터 관리자일 경우
     * 시스템에 등록된 모든 관리자 정보를 조회하여 반환합니다.
     *
     * - 마스터 관리자가 아닐 경우 예외가 발생합니다.
     * - 토큰이 유효하지 않거나 관리자 정보가 존재하지 않을 경우 예외가 발생합니다.
     *
     * @param authorizationHeader Authorization 헤더에 포함된 액세스 토큰
     * @return 전체 관리자 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<AdminUserDetailResponse> getAllAdminUserDetail(String authorizationHeader) {
        AdminUser adminUser = jwtUtil.getAdminUserFromHeader(authorizationHeader);
        if (!AdminStatus.MASTER.equals(adminUser.getAdminStatus())) {
            throw new CustomException(AdminErrorStatus._ONLY_CAN_MASTER_ADMIN_USER);
        }

        return adminRepository.findAll().stream()
                .map(AdminUserDetailResponse::from)
                .toList();
    }

    /**
     * 관리자 권한 상태 수정 메서드.
     *
     * 요청자의 토큰에서 관리자 정보를 추출하고, 마스터 관리자 권한을 확인합니다.
     * 대상 관리자 ID를 통해 조회 후, 요청된 권한 상태로 업데이트합니다.
     *
     * - 마스터 관리자가 아닐 경우 예외가 발생합니다.
     * - 대상 관리자가 존재하지 않을 경우 예외가 발생합니다.
     *
     * @param authorizationHeader 요청자의 액세스 토큰
     * @param request 수정할 관리자 ID와 변경할 권한 상태를 담은 요청 객체
     */
    @Transactional
    public void updateAdminUserStatus(String authorizationHeader, UpdateAdminUserStatusRequest request) {
        AdminUser adminUser = jwtUtil.getAdminUserFromHeader(authorizationHeader);
        if (!AdminStatus.MASTER.equals(adminUser.getAdminStatus())) {
            throw new CustomException(AdminErrorStatus._ONLY_CAN_MASTER_ADMIN_USER);
        }

        AdminUser targetAdminUser = adminRepository.findById(request.id())
                .orElseThrow(() -> new CustomException(AdminErrorStatus._NOT_FOUND_ADMIN_USER));
        targetAdminUser.updateAdminStatus(request.adminStatus());
    }

    /**
     * 관리자 계정 탈퇴 처리 메서드.
     *
     * 액세스 토큰을 기반으로 관리자 정보를 조회한 뒤 DB에서 삭제합니다.
     *
     * @param authorizationHeader Authorization 헤더에 포함된 액세스 토큰
     */
    @Transactional
    public void withdrawAdminUser(String authorizationHeader) {
        AdminUser adminUser = jwtUtil.getAdminUserFromHeader(authorizationHeader);
        adminRepository.delete(adminUser);
    }

    /**
     * 대시보드 이벤트 목록 조회 메서드
     *
     * 어드민 권한 사용자가 전체 이벤트 목록을 페이지 단위로 조회할 수 있습니다.
     * 정렬 기준으로 전달된 snake_case 필드명을 camelCase로 변환하여 동적으로 정렬하며,
     * 각 이벤트에 대해 스케줄 및 참여자 수를 일괄 조회한 뒤 DashboardEvent로 변환합니다.
     *
     * @param authorizationHeader Authorization 헤더에서 추출한 토큰
     * @param pageable 페이지 정보 (페이지 번호, 크기 등 - 정렬은 직접 처리)
     * @param keyword 정렬 기준 필드명 (snake_case)
     * @param sorting 정렬 방향 ("asc", "desc")
     * @return DashboardEvent 리스트 (페이징된 이벤트 정보)
     */
    @Transactional(readOnly = true)
    public List<DashboardEvent> getAllDashboardEvents(String authorizationHeader, Pageable pageable, String keyword, String sorting) {
        jwtUtil.getAdminUserFromHeader(authorizationHeader);

        boolean isSortByParticipant = keyword.equals("participant_count");
        List<Event> events = isSortByParticipant
                ? eventRepository.findAll()
                : eventRepository.findAllWithSort(pageable, keyword, sorting);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, List<Schedule>> scheduleMap = scheduleRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getEvent().getId()));

        // 1. 모든 EventParticipation 조회
        Map<Long, List<EventParticipation>> epMap = eventParticipationRepository.findAllByEventIdIn(eventIds).stream()
                .filter(ep -> ep.getEventStatus() != EventStatus.CREATOR)
                .collect(Collectors.groupingBy(ep -> ep.getEvent().getId()));

        // 2. 모든 Member 조회
        Map<Long, List<Member>> memberMap = memberRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(member -> member.getEvent().getId()));

        // 3. 참여자 수 계산
        List<DashboardEvent> dashboardEvents = events.stream()
                .map(event -> {
                    List<Schedule> schedules = scheduleMap.getOrDefault(event.getId(), List.of());
                    int userCount = epMap.getOrDefault(event.getId(), List.of()).size();
                    int memberCount = memberMap.getOrDefault(event.getId(), List.of()).size();
                    int totalParticipantCount = userCount + memberCount;

                    return DashboardEvent.of(event, schedules, totalParticipantCount);
                })
                .toList();

        if (isSortByParticipant) {
            Comparator<DashboardEvent> comparator = Comparator.comparingInt(DashboardEvent::participantCount);
            if (sorting.equalsIgnoreCase("desc")) comparator = comparator.reversed();
            dashboardEvents = dashboardEvents.stream()
                    .sorted(comparator)
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .toList();
        }

        return dashboardEvents;
    }

    /**
     * 대시보드 사용자 목록 조회 메서드
     *
     * 어드민 권한 사용자가 전체 사용자 정보를 페이지 단위로 조회할 수 있습니다.
     * 사용자 목록은 정렬 기준(keyword)과 정렬 방향(sorting)에 따라 정렬되며,
     * 각 사용자 데이터는 참여 이벤트 수를 포함한 DashboardUser DTO로 변환됩니다.
     *
     * @param authorizationHeader Authorization 헤더에서 추출한 토큰
     * @param pageable 페이지 정보 (페이지 번호, 크기 등)
     * @param keyword 정렬 기준 필드 (예: name, email, created_date 등)
     * @param sorting 정렬 방향 ("asc" 또는 "desc")
     * @return 정렬 및 페이징된 DashboardUser 리스트
     */
    @Transactional(readOnly = true)
    public List<DashboardUser> getAllDashboardUsers(String authorizationHeader, Pageable pageable, String keyword, String sorting) {
        AdminUser adminUser = jwtUtil.getAdminUserFromHeader(authorizationHeader);

        List<User> users = userRepository.findAllWithSort(pageable, keyword, sorting);

        return users.stream()
                .map(user -> {
                    int participantCount = eventParticipationRepository.findAllByUser(user).size();
                    return DashboardUser.from(user, participantCount);
                })
                .toList();
    }
}
