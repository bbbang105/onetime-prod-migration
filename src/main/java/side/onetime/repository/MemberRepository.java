package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Event;
import side.onetime.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Boolean existsByEventAndNameAndPin(Event event, String name, String pin);
    Boolean existsByEventAndName(Event event, String name);
    Optional<Member> findByEventAndNameAndPin(Event event, String name, String pin);
}