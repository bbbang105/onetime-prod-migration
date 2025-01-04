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

    /**
     * 특정 범위에 해당하는 스케줄 삭제 메서드.
     *
     * 이벤트와 연결된 특정 범위(DATE 또는 DAY)에 해당하는 모든 스케줄 및 관련 데이터를 삭제합니다.
     * 삭제 순서는 외래 키 제약 조건을 고려하여,
     * Selection → Schedule 순으로 진행됩니다.
     *
     * @param event 이벤트 객체
     * @param range 삭제할 범위 (DATE 또는 DAY)
     */
    @Override
    public void deleteSchedulesByRange(Event event, String range) {
        queryFactory.delete(selection)
                .where(selection.schedule.event.eq(event)
                        .and(selection.schedule.date.eq(range)
                                .or(selection.schedule.day.eq(range))))
                .execute();

        queryFactory.delete(schedule)
                .where(schedule.event.eq(event)
                        .and(schedule.date.eq(range).or(schedule.day.eq(range))))
                .execute();
    }

    /**
     * 특정 시간에 해당하는 스케줄 삭제 메서드.
     *
     * 이벤트와 연결된 특정 시간(HH:mm 형식)에 해당하는 모든 스케줄 및 관련 데이터를 삭제합니다.
     * 삭제 순서는 외래 키 제약 조건을 고려하여,
     * Selection → Schedule 순으로 진행됩니다.
     *
     * @param event 이벤트 객체
     * @param time 삭제할 시간 (HH:mm 형식)
     */
    @Override
    public void deleteSchedulesByTime(Event event, String time) {
        queryFactory.delete(selection)
                .where(selection.schedule.event.eq(event)
                        .and(selection.schedule.time.eq(time)))
                .execute();

        queryFactory.delete(schedule)
                .where(schedule.event.eq(event)
                        .and(schedule.time.eq(time)))
                .execute();
    }
}
