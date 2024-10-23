package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.FixedSelection;

public interface FixedSelectionRepository extends JpaRepository<FixedSelection, Long> {
}