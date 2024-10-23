package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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
    public List<FixedEvent> findAllByUser(User user) {
        return queryFactory.selectFrom(fixedEvent)
                .leftJoin(fixedEvent.fixedSelections, fixedSelection)
                .fetchJoin()
                .leftJoin(fixedSelection.fixedSchedule, fixedSchedule)
                .fetchJoin()
                .where(fixedEvent.user.eq(user))
                .fetch();
    }
}