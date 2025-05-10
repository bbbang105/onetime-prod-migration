package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedSchedule;
import side.onetime.domain.FixedSelection;
import side.onetime.domain.User;
import side.onetime.dto.fixed.request.UpdateFixedScheduleRequest;
import side.onetime.dto.fixed.response.FixedScheduleResponse;
import side.onetime.dto.fixed.response.GetFixedScheduleResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.FixedErrorStatus;
import side.onetime.exception.status.UserErrorStatus;
import side.onetime.repository.FixedScheduleRepository;
import side.onetime.repository.FixedSelectionRepository;
import side.onetime.repository.UserRepository;
import side.onetime.util.UserAuthorizationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixedScheduleService {
    private final UserRepository userRepository;
    private final FixedScheduleRepository fixedScheduleRepository;
    private final FixedSelectionRepository fixedSelectionRepository;

    /**
     * 유저의 고정 스케줄 조회 메서드.
     *
     * 유저의 모든 고정 스케줄을 조회하여 요일별 그룹화하여 반환합니다.
     *
     * @return 유저의 고정 스케줄 목록
     */
    @Transactional(readOnly = true)
    public GetFixedScheduleResponse getUserFixedSchedule() {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        List<FixedSelection> fixedSelections = fixedSelectionRepository.findAllByUser(user);

        Map<String, List<String>> groupedSchedules = fixedSelections.stream()
                .collect(Collectors.groupingBy(
                        selection -> selection.getFixedSchedule().getDay(),
                        Collectors.mapping(selection -> selection.getFixedSchedule().getTime(), Collectors.toList())
                ));

        List<FixedScheduleResponse> scheduleResponses = groupedSchedules.entrySet().stream()
                .map(entry -> FixedScheduleResponse.of(entry.getKey(), entry.getValue()))
                .toList();

        return new GetFixedScheduleResponse(scheduleResponses);
    }

    /**
     * 유저의 고정 스케줄 수정 메서드.
     *
     * 기존에 저장된 유저의 고정 스케줄을 모두 삭제한 후, 새로운 스케줄을 등록합니다.
     *
     * @param request 유저가 입력한 새로운 스케줄 목록
     */
    @Transactional
    public void updateUserFixedSchedules(UpdateFixedScheduleRequest request) {
        User user = userRepository.findById(UserAuthorizationUtil.getLoginUserId())
                .orElseThrow(() -> new CustomException(UserErrorStatus._NOT_FOUND_USER));
        // 기존 고정 스케줄 삭제
        fixedSelectionRepository.deleteFixedSelectionsByUser(user);

        List<FixedSelection> newFixedSelections = new ArrayList<>();

        for (FixedScheduleResponse fixedScheduleResponse : request.schedules()) {
            String day = fixedScheduleResponse.timePoint();
            List<String> times = fixedScheduleResponse.times();

            List<FixedSchedule> fixedSchedules = fixedScheduleRepository.findAllByDay(day)
                    .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_SCHEDULES));

            for (FixedSchedule fixedSchedule : fixedSchedules) {
                if (times.contains(fixedSchedule.getTime())) {
                    newFixedSelections.add(FixedSelection.builder()
                            .user(user)
                            .fixedSchedule(fixedSchedule)
                            .build());
                }
            }
        }

        fixedSelectionRepository.saveAll(newFixedSelections);
    }
}
