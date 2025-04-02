package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import side.onetime.domain.QEvent;
import side.onetime.domain.QEventParticipation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EventParticipationRepositoryImpl implements EventParticipationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 이벤트 ID 리스트를 기반으로 각 이벤트별 참여자 수를 조회합니다.
     *
     * @param eventIds 이벤트 식별자 리스트 (eventId - String)
     * @return 각 이벤트 ID에 대응하는 참여자 수 Map
     */
    @Override
    public Map<Long, Integer> countParticipantsByEventIds(List<Long> eventIds) {
        QEventParticipation ep = QEventParticipation.eventParticipation;
        QEvent e = QEvent.event;

        return queryFactory
                .select(e.id, ep.id.count())
                .from(ep)
                .join(ep.event, e)
                .where(e.id.in(eventIds))
                .groupBy(e.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(e.id),
                        tuple -> Math.toIntExact(tuple.get(ep.id.count()))
                ));
    }
}
