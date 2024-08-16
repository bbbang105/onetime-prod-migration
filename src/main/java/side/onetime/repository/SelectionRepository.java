package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Member;
import side.onetime.domain.Selection;

public interface SelectionRepository extends JpaRepository<Selection, Long> {
    void deleteAllByMember(Member member);
}