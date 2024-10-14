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

    @Override
    public void deleteUserEvent(Event e) {
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