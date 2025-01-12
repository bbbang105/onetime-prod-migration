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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixedScheduleService {
    private final FixedEventRepository fixedEventRepository;
    private final FixedScheduleRepository fixedScheduleRepository;
    private final FixedSelectionRepository fixedSelectionRepository;

    /**
     * 고정 스케줄 등록 메서드.
     *
     * 주어진 고정 이벤트에 대해 요청된 스케줄 데이터를 등록합니다.
     * FixedSelection을 생성하고 데이터베이스에 저장합니다.
     *
     * @param fixedScheduleResponses 등록할 스케줄 데이터
     * @param fixedEvent 고정 이벤트 객체
     */
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

    /**
     * 전체 고정 스케줄 조회 메서드.
     *
     * 인증된 사용자의 모든 고정 스케줄 데이터를 조회합니다.
     * FixedEventResponse 리스트로 반환합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 고정 스케줄 응답 데이터 리스트
     */
    @Transactional(readOnly = true)
    public List<FixedEventResponse> getAllFixedSchedules(User user) {
        List<FixedEvent> fixedEvents = fixedEventRepository.findAllByUser(user);

        return fixedEvents.stream()
                .map(fixedEvent -> {
                    Map<String, List<String>> groupedSchedules = fixedEvent.getFixedSelections().stream()
                            .collect(Collectors.groupingBy(
                                    selection -> selection.getFixedSchedule().getDay(),
                                    Collectors.collectingAndThen(
                                            Collectors.mapping(selection -> selection.getFixedSchedule().getTime(), Collectors.toList()),
                                            list -> {
                                                list.sort(Comparator.naturalOrder());
                                                return list;
                                            }
                                    )
                            ));

                    List<FixedScheduleResponse> scheduleResponses = groupedSchedules.entrySet().stream()
                            .map(entry -> FixedScheduleResponse.of(entry.getKey(), entry.getValue()))
                            .toList();

                    return FixedEventResponse.of(fixedEvent.getId(), scheduleResponses);
                })
                .toList();
    }

    /**
     * 특정 고정 스케줄 상세 조회 메서드.
     *
     * @param user 인증된 사용자 정보
     * @param fixedEventId 조회할 고정 이벤트 ID
     * @return 고정 이벤트 상세 데이터
     */
    @Transactional(readOnly = true)
    public FixedEventDetailResponse getFixedScheduleDetail(User user, Long fixedEventId) {
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndFixedEventIdCustom(user, fixedEventId);
        if (fixedEvent == null) {
            throw new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT);
        }

        Map<String, List<String>> groupedSchedules = fixedEvent.getFixedSelections().stream()
                .collect(Collectors.groupingBy(
                        selection -> selection.getFixedSchedule().getDay(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(selection -> selection.getFixedSchedule().getTime(), Collectors.toList()),
                                list -> {
                                    list.sort(Comparator.naturalOrder());
                                    return list;
                                }
                        )
                ));

        List<FixedScheduleResponse> scheduleResponses = groupedSchedules.entrySet().stream()
                .map(entry -> FixedScheduleResponse.of(entry.getKey(), entry.getValue()))
                .toList();

        return FixedEventDetailResponse.of(fixedEvent.getTitle(), scheduleResponses);
    }

    /**
     * 고정 스케줄 수정 메서드.
     *
     * 기존 고정 이벤트의 스케줄 데이터를 삭제하고,
     * 새로운 스케줄 데이터를 기반으로 수정합니다.
     *
     * @param user 인증된 사용자 정보
     * @param fixedEventId 수정할 고정 이벤트 ID
     * @param modifyFixedEventRequest 수정 요청 데이터
     */
    @Transactional
    public void modifyFixedSchedule(User user, Long fixedEventId, ModifyFixedEventRequest modifyFixedEventRequest) {
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId)
                .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT));

        fixedSelectionRepository.deleteFixedSelectionsByEvent(fixedEvent);
        createFixedSchedules(modifyFixedEventRequest.schedules(), fixedEvent);
    }
}
