package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;
import side.onetime.dto.fixed.request.CreateFixedEventRequest;
import side.onetime.dto.fixed.request.ModifyFixedEventRequest;
import side.onetime.dto.fixed.response.FixedEventByDayResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.FixedErrorStatus;
import side.onetime.repository.FixedEventRepository;
import side.onetime.util.JwtUtil;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FixedEventService {
    private final FixedScheduleService fixedScheduleService;
    private final FixedEventRepository fixedEventRepository;
    private final JwtUtil jwtUtil;

    // 고정 이벤트 생성 메서드
    @Transactional
    public void createFixedEvent(String authorizationHeader, CreateFixedEventRequest createFixedEventRequest) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        List<String> times = createFixedEventRequest.schedules().get(0).times();
        String startTime = times.get(0);
        String endTime = times.get(times.size() - 1);
        FixedEvent fixedEvent = createFixedEventRequest.toEntity(user, startTime, endTime);
        fixedEventRepository.save(fixedEvent);
        fixedScheduleService.createFixedSchedules(createFixedEventRequest.schedules(), fixedEvent);
    }

    // 고정 이벤트 수정 메서드
    @Transactional
    public void modifyFixedEvent(String authorizationHeader, Long fixedEventId, ModifyFixedEventRequest modifyFixedEventRequest) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId)
                .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT));
        fixedEvent.updateTitle(modifyFixedEventRequest.title());
        fixedEventRepository.save(fixedEvent);
    }

    // 고정 이벤트 & 스케줄 삭제 메서드
    @Transactional
    public void removeFixedEvent(String authorizationHeader, Long fixedEventId) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId)
                .orElseThrow(() -> new CustomException(FixedErrorStatus._NOT_FOUND_FIXED_EVENT));
        fixedEventRepository.deleteFixedEventAndSelections(user, fixedEventId);
    }

    // 요일 별 고정 이벤트 조회 메서드
    @Transactional(readOnly = true)
    public List<FixedEventByDayResponse> getFixedEventByDay(String authorizationHeader, String day) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        String koreanDay = convertDayToKorean(day);

        List<FixedEvent> fixedEvents = fixedEventRepository.findFixedEventsByUserAndDay(user, koreanDay);

        return fixedEvents.stream()
                .map(fixedEvent -> FixedEventByDayResponse.of(
                        fixedEvent.getId(),
                        fixedEvent.getTitle(),
                        fixedEvent.getStartTime(),
                        fixedEvent.getEndTime()
                ))
                .toList();
    }

    // 영어 요일 -> 한글 요일 변환 메서드
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