package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import side.onetime.domain.Event;

import static side.onetime.domain.QEvent.event;
import static side.onetime.domain.QEventParticipation.eventParticipation;
import static side.onetime.domain.QMember.member;
import static side.onetime.domain.QSchedule.schedule;
import static side.onetime.domain.QSelection.selection;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 이벤트 삭제 메서드.
     *
     * 이벤트에 연결된 모든 관련 데이터를 삭제합니다.
     * 삭제 순서는 외래 키 제약 조건을 고려하여,
     * Selection → EventParticipation → Schedule → Member → Event 순으로 진행됩니다.
     *
     * @param e 삭제할 Event 객체
     */
    @Override
    public void deleteEvent(Event e) {
        queryFactory.delete(selection)
                .where(selection.schedule.event.eq(e))
                .execute();

        queryFactory.delete(eventParticipation)
                .where(eventParticipation.event.eq(e))
                .execute();

        queryFactory.delete(schedule)
                .where(schedule.event.eq(e))
                .execute();

        queryFactory.delete(member)
                .where(member.event.eq(e))
                .execute();

        queryFactory.delete(event)
                .where(event.eq(e))
                .execute();
    }
}
