package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
}