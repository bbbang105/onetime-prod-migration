package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import side.onetime.domain.Event;
import side.onetime.domain.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEventAndNameAndPin(Event event, String name, String pin);
    Boolean existsByEventAndName(Event event, String name);
    Optional<Member> findByEventAndNameAndPin(Event event, String name, String pin);
    Optional<Member> findByMemberId(UUID memberId);
    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.selections s " +
            "JOIN FETCH s.schedule sch " +
            "WHERE m.event = :event")
    List<Member> findAllWithSelectionsAndSchedulesByEvent(@Param("event") Event event);
}