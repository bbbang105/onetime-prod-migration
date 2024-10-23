package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.FixedSchedule;

import java.util.List;
import java.util.Optional;

public interface FixedScheduleRepository extends JpaRepository<FixedSchedule, Long> {
    Optional<List<FixedSchedule>> findAllByDay(String day);
}