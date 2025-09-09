package side.onetime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import side.onetime.domain.*;

import java.util.List;

public interface SelectionRepository extends JpaRepository<Selection, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Selection s WHERE s.member = :member")
    void deleteAllByMember(@Param("member") Member member);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Selection s WHERE s.user = :user AND s.schedule.event = :event")
    void deleteAllByUserAndEvent(@Param("user") User user, @Param("event") Event event);

    @Query("""
        SELECT s FROM Selection s
        JOIN FETCH s.schedule sc
        WHERE sc.event = :event
    """)
    List<Selection> findAllSelectionsByEvent(@Param("event") Event event);

    @Query("""
        SELECT COUNT(s) > 0 FROM Selection s
        WHERE s.user = :user
        AND s.schedule.event = :event
    """)
    boolean existsByUserAndEventSchedules(@Param("user") User user, @Param("event") Event event);

    @Query("""
        SELECT s FROM Selection s
        JOIN FETCH s.schedule sc
        WHERE s.member = :member
    """)
    List<Selection> findAllByMemberWithSchedule(@Param("member") Member member);

    @Query("""
        SELECT s FROM Selection s
        JOIN FETCH s.schedule sc
        JOIN FETCH sc.event e
        WHERE e = :event AND (s.user.id IN :userIds OR s.member.id IN :memberIds)
    """)
    List<Selection> findAllByUserIdsOrMemberIdsWithScheduleAndEvent(@Param("event") Event event, @Param("userIds") List<Long> userIds, @Param("memberIds") List<Long> memberIds);

    @Query("""
        SELECT s FROM Selection s
        JOIN FETCH s.schedule sc
        JOIN FETCH sc.event e
        WHERE s.user = :user AND e = :event
    """)
    List<Selection> findAllByUserAndEventWithScheduleAndEvent(@Param("user") User user, @Param("event") Event event);
}
