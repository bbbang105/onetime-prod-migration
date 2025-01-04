package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import side.onetime.domain.FixedEvent;

import static side.onetime.domain.QFixedSelection.fixedSelection;

@RequiredArgsConstructor
public class FixedSelectionRepositoryImpl implements FixedSelectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 고정 이벤트 기반 고정 선택 스케줄 삭제 메서드.
     *
     * 주어진 고정 이벤트에 연결된 모든 고정 선택 데이터를 삭제합니다.
     * @param fixedEvent 삭제할 고정 선택 데이터의 기준이 되는 고정 이벤트 객체
     */
    @Override
    public void deleteFixedSelectionsByEvent(FixedEvent fixedEvent) {
        queryFactory.delete(fixedSelection)
                .where(fixedSelection.fixedEvent.eq(fixedEvent))
                .execute();
    }
}
