package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;

import java.util.List;

import static side.onetime.domain.QFixedEvent.fixedEvent;
import static side.onetime.domain.QFixedSchedule.fixedSchedule;
import static side.onetime.domain.QFixedSelection.fixedSelection;

@RequiredArgsConstructor
public class FixedEventRepositoryImpl implements FixedEventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 유저 고정 스케줄 목록 조회 메서드.
     *
     * 특정 유저의 모든 고정 이벤트 및 관련 고정 선택, 고정 스케줄 데이터를 조회합니다.
     * @param user 조회할 유저 객체
     * @return 유저의 고정 이벤트 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<FixedEvent> findAllByUser(User user) {
        return queryFactory.selectFrom(fixedEvent)
                .leftJoin(fixedEvent.fixedSelections, fixedSelection)
                .fetchJoin()
                .leftJoin(fixedSelection.fixedSchedule, fixedSchedule)
                .fetchJoin()
                .where(fixedEvent.user.eq(user))
                .fetch();
    }

    /**
     * 특정 고정 스케줄 상세 조회 메서드.
     *
     * 특정 유저와 고정 이벤트 ID를 기반으로 고정 이벤트 데이터를 조회합니다.
     * 고정 이벤트와 관련된 고정 선택 및 고정 스케줄 데이터를 포함하여 반환합니다.
     * @param user 조회할 유저 객체
     * @param fixedEventId 조회할 고정 이벤트 ID
     * @return 고정 이벤트 객체
     */
    @Override
    @Transactional(readOnly = true)
    public FixedEvent findByUserAndFixedEventIdCustom(User user, Long fixedEventId) {
        return queryFactory.selectFrom(fixedEvent)
                .leftJoin(fixedEvent.fixedSelections, fixedSelection)
                .fetchJoin()
                .leftJoin(fixedSelection.fixedSchedule, fixedSchedule)
                .fetchJoin()
                .where(fixedEvent.user.eq(user)
                        .and(fixedEvent.id.eq(fixedEventId)))
                .fetchOne();
    }

    /**
     * 고정 이벤트 및 스케줄 삭제 메서드.
     *
     * 특정 유저와 고정 이벤트 ID를 기반으로 고정 선택 데이터를 먼저 삭제하고,
     * 이후 고정 이벤트 데이터를 삭제합니다.
     * @param user 삭제할 유저 객체
     * @param fixedEventId 삭제할 고정 이벤트 ID
     */
    @Override
    @Transactional
    public void deleteFixedEventAndSelections(User user, Long fixedEventId) {
        // fixed_selections 삭제
        queryFactory.delete(fixedSelection)
                .where(fixedSelection.fixedEvent.id.eq(fixedEventId)
                        .and(fixedSelection.fixedEvent.user.eq(user)))
                .execute();

        // fixed_events 삭제
        queryFactory.delete(fixedEvent)
                .where(fixedEvent.id.eq(fixedEventId)
                        .and(fixedEvent.user.eq(user)))
                .execute();
    }

    /**
     * 요일별 고정 이벤트 조회 메서드.
     *
     * 특정 유저와 요일을 기준으로 고정 이벤트 데이터를 조회합니다.
     * 고정 이벤트와 관련된 고정 선택 및 고정 스케줄 데이터를 포함하여 반환합니다.
     * @param user 조회할 유저 객체
     * @param day 조회할 요일 (예: "월", "화" 등)
     * @return 요일에 해당하는 고정 이벤트 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<FixedEvent> findFixedEventsByUserAndDay(User user, String day) {
        return queryFactory.selectFrom(fixedEvent)
                .leftJoin(fixedEvent.fixedSelections, fixedSelection)
                .fetchJoin()
                .leftJoin(fixedSelection.fixedSchedule, fixedSchedule)
                .fetchJoin()
                .where(fixedEvent.user.eq(user)
                        .and(fixedSchedule.day.eq(day)))
                .fetch();
    }
}
