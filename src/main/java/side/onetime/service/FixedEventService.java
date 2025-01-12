package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.FixedSelection;
import side.onetime.domain.User;
import side.onetime.dto.fixed.request.CreateFixedEventRequest;
import side.onetime.dto.fixed.request.ModifyFixedEventRequest;
import side.onetime.dto.fixed.response.FixedEventByDayResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.FixedErrorStatus;
import side.onetime.repository.FixedEventRepository;
import side.onetime.util.DateUtil;

import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FixedEventService {
    private final FixedScheduleService fixedScheduleService;
    private final FixedEventRepository fixedEventRepository;

    /**
     * 고정 이벤트 생성 메서드.
     *
     * 유저 인증 정보를 기반으로 새로운 고정 이벤트를 생성하고,
     * 관련된 고정 스케줄을 등록합니다.
     *
     * @param user 인증된 사용자 정보
     * @param createFixedEventRequest 고정 이벤트 생성 요청 데이터
     */
    @Transactional
    public void createFixedEvent(User user, CreateFixedEventRequest createFixedEventRequest) {
        FixedEvent fixedEvent = createFixedEventRequest.toEntity(user);
        fixedEventRepository.save(fixedEvent);
        fixedScheduleService.createFixedSchedules(createFixedEventRequest.schedules(), fixedEvent);
    }

    /**
     * 고정 이벤트 및 관련 스케줄 삭제 메서드.
     *
     * 고정 이벤트와 해당 이벤트에 관련된 모든 스케줄을 삭제합니다.
     *
     * @param user 인증된 사용자 정보
     * @param fixedEventId 삭제할 고정 이벤트 ID
     */
    @Transactional
    public void removeFixedEvent(User user, Long fixedEventId) {
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId)
                .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT));
        fixedEventRepository.deleteFixedEventAndSelections(user, fixedEventId);
    }

    /**
     * 고정 이벤트 수정 메서드.
     *
     * 고정 이벤트의 제목을 수정합니다.
     *
     * @param user 인증된 사용자 정보
     * @param fixedEventId 수정할 고정 이벤트 ID
     * @param modifyFixedEventRequest 고정 이벤트 수정 요청 데이터
     */
    @Transactional
    public void modifyFixedEvent(User user, Long fixedEventId, ModifyFixedEventRequest modifyFixedEventRequest) {
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId)
                .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT));

        fixedEvent.updateTitle(modifyFixedEventRequest.title());
        fixedEventRepository.save(fixedEvent);
    }

    /**
     * 요일별 고정 이벤트 조회 메서드.
     *
     * 특정 요일에 해당하는 고정 이벤트 목록을 조회합니다.
     * startTime을 기준으로 오름차순 정렬하여 반환합니다.
     *
     * @param user 인증된 사용자 정보
     * @param day 조회할 요일 (예: "mon", "tue" 등)
     * @return 고정 이벤트 목록 (startTime 기준 오름차순 정렬)
     */
    @Transactional(readOnly = true)
    public List<FixedEventByDayResponse> getFixedEventByDay(User user, String day) {
        String koreanDay = convertDayToKorean(day);

        return fixedEventRepository.findFixedEventsByUserAndDay(user, koreanDay).stream()
                .map(fixedEvent -> {
                    List<FixedSelection> fixedSelections = fixedEvent.getFixedSelections();
                    String startTime = fixedSelections.get(0).getFixedSchedule().getTime();
                    String endTime = fixedSelections.get(fixedSelections.size() - 1).getFixedSchedule().getTime();
                    return FixedEventByDayResponse.of(
                            fixedEvent.getId(),
                            fixedEvent.getTitle(),
                            startTime,
                            DateUtil.addThirtyMinutes(endTime)
                    );
                })
                .sorted(Comparator.comparing(FixedEventByDayResponse::startTime))
                .toList();
    }

    /**
     * 영어 요일 -> 한글 요일 변환 메서드.
     *
     * 영어로 입력된 요일을 한글 요일로 변환합니다.
     * 입력이 올바르지 않을 경우 예외를 발생시킵니다.
     *
     * @param day 영어 요일 (예: "mon", "tue" 등)
     * @return 한글 요일 (예: "월", "화" 등)
     */
    private String convertDayToKorean(String day) {
        return switch (day.toLowerCase()) {
            case "mon" -> "월";
            case "tue" -> "화";
            case "wed" -> "수";
            case "thu" -> "목";
            case "fri" -> "금";
            case "sat" -> "토";
            case "sun" -> "일";
            default -> throw new CustomException(FixedErrorStatus._IS_NOT_RIGHT_DAY);
        };
    }
}
