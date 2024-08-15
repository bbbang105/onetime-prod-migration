package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Event;
import side.onetime.domain.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    Optional<List<Schedule>> findAllByEventAndDay(Event event, String day);
    Optional<List<Schedule>> findAllByEventAndDate(Event event, String date);
}