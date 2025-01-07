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
    Boolean existsByEventAndName(Event event, String name);
    Optional<Member> findByEventAndNameAndPin(Event event, String name, String pin);
    Optional<Member> findByMemberId(UUID memberId);

    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.selections s " +
            "JOIN FETCH s.schedule sch " +
            "WHERE m.event = :event")
    List<Member> findAllWithSelectionsAndSchedulesByEvent(@Param("event") Event event);

    @Query("SELECT m FROM Member m JOIN FETCH m.selections WHERE m.memberId = :memberId")
    Optional<Member> findByMemberIdWithSelections(@Param("memberId") UUID memberId);

    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.selections s " +
            "JOIN FETCH s.schedule sch " +
            "WHERE m.event = :event AND m.name IN :names")
    List<Member> findAllWithSelectionsAndSchedulesByEventAndNames(@Param("event") Event event, @Param("names") List<String> names);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.event = :event")
    int countByEvent(@Param("event") Event event);
}
