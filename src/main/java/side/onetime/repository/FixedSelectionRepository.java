package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.FixedSelection;
import side.onetime.repository.custom.FixedSelectionRepositoryCustom;

public interface FixedSelectionRepository extends JpaRepository<FixedSelection, Long>, FixedSelectionRepositoryCustom {
}