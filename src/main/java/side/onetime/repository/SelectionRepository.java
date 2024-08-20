package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.domain.Selection;

import java.util.List;

public interface SelectionRepository extends JpaRepository<Selection, Long> {
    void deleteAllByMember(Member member);
    @Query("SELECT s FROM Selection s JOIN FETCH s.schedule sc WHERE sc.event = :event")
    List<Selection> findAllSelectionsByEvent(@Param("event") Event event);
}