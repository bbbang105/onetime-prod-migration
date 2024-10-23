package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.FixedEvent;

public interface FixedEventRepository extends JpaRepository<FixedEvent, Long> {
}