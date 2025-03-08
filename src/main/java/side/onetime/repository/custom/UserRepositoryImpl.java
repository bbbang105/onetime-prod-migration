package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import side.onetime.domain.*;
import side.onetime.domain.enums.EventStatus;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 유저 서비스 탈퇴 메서드.
     *
     * 인증된 유저의 계정을 삭제하고,
     * 유저가 생성한(즉, EventParticipation의 상태가 PARTICIPANT가 아닌) 이벤트를 함께 삭제합니다.
     *
     * 삭제 순서: Selection → EventParticipation → Schedule → Member → Event
     *
     * @param user 탈퇴할 유저
     */
    @Override
    public void withdraw(User user) {
        // 유저가 생성한 이벤트(참여 상태가 PARTICIPANT가 아닌)를 조회
        List<EventParticipation> participations = queryFactory
                .selectFrom(QEventParticipation.eventParticipation)
                .where(
                        QEventParticipation.eventParticipation.user.eq(user)
                                .and(QEventParticipation.eventParticipation.eventStatus.ne(EventStatus.PARTICIPANT))
                )
                .fetch();

        // 삭제 쿼리 수행.
        for (EventParticipation participation : participations) {
            Event event = participation.getEvent();

            queryFactory.delete(QSelection.selection)
                    .where(QSelection.selection.schedule.event.eq(event))
                    .execute();

            queryFactory.delete(QEventParticipation.eventParticipation)
                    .where(QEventParticipation.eventParticipation.event.eq(event))
                    .execute();

            queryFactory.delete(QSchedule.schedule)
                    .where(QSchedule.schedule.event.eq(event))
                    .execute();

            queryFactory.delete(QMember.member)
                    .where(QMember.member.event.eq(event))
                    .execute();

            queryFactory.delete(QEvent.event)
                    .where(QEvent.event.eq(event))
                    .execute();
        }

        queryFactory.delete(QFixedSelection.fixedSelection)
                .where(QFixedSelection.fixedSelection.user.eq(user))
                .execute();

        queryFactory.delete(QUser.user)
                .where(QUser.user.eq(user))
                .execute();
    }
}
