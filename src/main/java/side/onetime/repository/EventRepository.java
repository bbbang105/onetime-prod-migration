package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Event;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event,Long> {
    Optional<Event> findByEventId(UUID eventId);
    boolean existsByEventId(UUID eventId);
}