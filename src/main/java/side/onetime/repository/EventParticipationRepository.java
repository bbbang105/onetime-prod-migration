package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import side.onetime.domain.Event;
import side.onetime.domain.EventParticipation;
import side.onetime.domain.User;

import java.util.List;
import java.util.Optional;

public interface EventParticipationRepository extends JpaRepository<EventParticipation,Long> {
    List<EventParticipation> findAllByEvent(Event event);
    List<EventParticipation> findAllByUser(User user);
    @Query("SELECT COUNT(ep) FROM EventParticipation ep WHERE ep.event = :event")
    int countByEvent(@Param("event") Event event);
    Optional<EventParticipation> findByUserAndEvent(User user, Event event);
}