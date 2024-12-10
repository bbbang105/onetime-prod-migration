package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Event;
import side.onetime.repository.custom.EventRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event,Long>, EventRepositoryCustom {
    Optional<Event> findByEventId(UUID eventId);
    boolean existsByEventId(UUID eventId);
    List<Event> findByCreatedDateBefore(LocalDateTime twoWeeksAgo);
}
