package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;
import side.onetime.repository.custom.FixedEventRepositoryCustom;

import java.util.Optional;

public interface FixedEventRepository extends JpaRepository<FixedEvent, Long>, FixedEventRepositoryCustom {
    Optional<FixedEvent> findByUserAndId(User user, Long id);
}
