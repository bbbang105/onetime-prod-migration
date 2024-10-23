package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.FixedEvent;
import side.onetime.repository.custom.FixedEventRepositoryCustom;

public interface FixedEventRepository extends JpaRepository<FixedEvent, Long>, FixedEventRepositoryCustom {
}