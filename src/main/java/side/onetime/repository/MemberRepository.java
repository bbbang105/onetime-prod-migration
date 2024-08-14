package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import side.onetime.domain.Member;

public interface MemberRepository extends JpaRepository<Member,Long> {
}