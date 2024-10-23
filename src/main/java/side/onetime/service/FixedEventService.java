package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;
import side.onetime.dto.fixed.request.CreateFixedEventRequest;
import side.onetime.dto.fixed.request.ModifyFixedEventRequest;
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
        FixedEvent fixedEvent = fixedEventRepository.findByUserAndId(user, fixedEventId);
        fixedEvent.updateTitle(modifyFixedEventRequest.title());
        fixedEventRepository.save(fixedEvent);
    }
}