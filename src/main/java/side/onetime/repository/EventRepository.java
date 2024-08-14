package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Event;

public interface EventRepository extends JpaRepository<Event,Long> {
}