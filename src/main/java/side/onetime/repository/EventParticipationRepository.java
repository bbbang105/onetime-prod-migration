package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.EventParticipation;

public interface EventParticipationRepository extends JpaRepository<EventParticipation,Long> {
}