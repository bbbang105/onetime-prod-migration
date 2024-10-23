package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.FixedSchedule;
import side.onetime.domain.FixedSelection;
import side.onetime.domain.User;
import side.onetime.dto.fixed.request.ModifyFixedEventRequest;
import side.onetime.dto.fixed.response.FixedEventDetailResponse;
import side.onetime.dto.fixed.response.FixedEventResponse;
import side.onetime.dto.fixed.response.FixedScheduleResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.FixedErrorStatus;
import side.onetime.repository.FixedEventRepository;
import side.onetime.repository.FixedScheduleRepository;
import side.onetime.repository.FixedSelectionRepository;
import side.onetime.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static side.onetime.util.DateUtil.addThirtyMinutes;

@Service
@RequiredArgsConstructor
public class FixedScheduleService {
    private final FixedEventRepository fixedEventRepository;
    private final FixedScheduleRepository fixedScheduleRepository;
    private final FixedSelectionRepository fixedSelectionRepository;
    private final JwtUtil jwtUtil;

    // 고정 스케줄 등록 메서드
    @Transactional
    public void createFixedSchedules(List<FixedScheduleResponse> fixedScheduleResponses, FixedEvent fixedEvent) {
        List<FixedSelection> fixedSelections = new ArrayList<>();

        for (FixedScheduleResponse fixedScheduleResponse : fixedScheduleResponses) {
            String day = fixedScheduleResponse.timePoint();
            List<String> times = fixedScheduleResponse.times();
            List<FixedSchedule> fixedSchedules = fixedScheduleRepository.findAllByDay(day)
                    .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_SCHEDULES));

            for (FixedSchedule fixedSchedule : fixedSchedules) {
                if (times.contains(fixedSchedule.getTime())) {
                    fixedSelections.add(FixedSelection.builder()
                            .fixedEvent(fixedEvent)
                            .fixedSchedule(fixedSchedule)
                            .build());
                }
            }
        }
        fixedSelectionRepository.saveAll(fixedSelections);
    }

    // 전체 고정 스케줄 조회 메서드
    @Transactional(readOnly = true)
    public List<FixedEventResponse> getAllFixedSchedules(String authorizationHeader) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        List<FixedEvent> fixedEvents = fixedEventRepository.findAllByUser(user);

        if (fixedEvents.isEmpty()) {
            throw new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENTS);
        }

        List<FixedEventResponse> fixedEventResponses = new ArrayList<>();
        for (FixedEvent fixedEvent : fixedEvents) {
            // 각 이벤트에 대한 고정 선택을 그룹화하여 요일별 스케줄을 생성
            Map<String, List<String>> groupedSchedules = fixedEvent.getFixedSelections().stream()
                    .collect(Collectors.groupingBy(
                            selection -> selection.getFixedSchedule().getDay(),
                            Collectors.mapping(selection -> selection.getFixedSchedule().getTime(), Collectors.toList())
                    ));

            // 고정 스케줄 정보 생성
            List<FixedScheduleResponse> scheduleResponses = groupedSchedules.entrySet().stream()
                    .map(entry -> FixedScheduleResponse.of(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());

            // 고정 이벤트 정보 생성
            FixedEventResponse fixedEventResponse = FixedEventResponse.of(fixedEvent.getId(), fixedEvent.getStartTime(), fixedEvent.getEndTime(), scheduleResponses);
            fixedEventResponses.add(fixedEventResponse);
        }

        return fixedEventResponses;
    }

    // 특정 고정 스케줄 상세 조회 메서드
    @Transactional(readOnly = true)
    public FixedEventDetailResponse getFixedScheduleDetail(String authorizationHeader, Long fixedEventId) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        // 고정 이벤트 조회
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndFixedEventIdCustom(user, fixedEventId);
        if (fixedEvent == null) {
            throw new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT);
        }

        // 고정 선택을 요일별로 그룹화하여 시간 목록을 생성
        Map<String, List<String>> groupedSchedules = fixedEvent.getFixedSelections().stream()
                .collect(Collectors.groupingBy(
                        selection -> selection.getFixedSchedule().getDay(),
                        Collectors.mapping(selection -> selection.getFixedSchedule().getTime(), Collectors.toList())
                ));

        // 고정 스케줄 정보 생성
        List<FixedScheduleResponse> scheduleResponses = groupedSchedules.entrySet().stream()
                .map(entry -> FixedScheduleResponse.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // 고정 이벤트 상세 정보 반환
        return FixedEventDetailResponse.of(fixedEvent.getTitle(), fixedEvent.getStartTime(), fixedEvent.getEndTime(), scheduleResponses);
    }

    // 고정 스케줄 수정 메서드
    @Transactional
    public void modifyFixedSchedule(String authorizationHeader, Long fixedEventId, ModifyFixedEventRequest modifyFixedEventRequest) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId)
                .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT));

        List<String> times = modifyFixedEventRequest.schedules().get(0).times();
        String startTime = times.get(0);
        String endTime = times.get(times.size() - 1);
        fixedEvent.updateStartTime(startTime);
        fixedEvent.updateEndTime(addThirtyMinutes(endTime));
        fixedEventRepository.save(fixedEvent);

        fixedSelectionRepository.deleteFixedSelectionsByEvent(fixedEvent);
        createFixedSchedules(modifyFixedEventRequest.schedules(), fixedEvent);
    }
}