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

    // 유저 고정 스케줄 목록 조회
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

    // 특정 고정 스케줄 상세 조회
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

    // 고정 이벤트 & 스케줄 삭제
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
}