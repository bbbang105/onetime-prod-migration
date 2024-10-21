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

    @Scheduled(cron = "${scheduling.cron}")
    @Transactional
    public void deleteOldEvents() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 30일 이상 지난 이벤트를 찾은 후 삭제
        List<Event> oldEvents = eventRepository.findByCreatedDateBefore(thirtyDaysAgo);
        oldEvents.forEach(eventRepository::deleteEvent);
    }
}