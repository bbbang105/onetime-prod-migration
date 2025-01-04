package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import side.onetime.domain.Event;
import side.onetime.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final EventRepository eventRepository;

    /**
     * 오래된 이벤트 삭제 스케줄러.
     *
     * 매일 정해진 시간에 실행되어, 30일 이상 지난 이벤트 데이터를 삭제합니다.
     * 삭제 기준은 이벤트 생성일(createdDate)이며, cron 표현식은 설정 파일에 정의됩니다.
     */
    @Scheduled(cron = "${scheduling.cron}")
    @Transactional
    public void deleteOldEvents() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 30일 이상 지난 이벤트를 찾은 후 삭제.
        List<Event> oldEvents = eventRepository.findByCreatedDateBefore(thirtyDaysAgo);
        oldEvents.forEach(eventRepository::deleteEvent);
    }
}
