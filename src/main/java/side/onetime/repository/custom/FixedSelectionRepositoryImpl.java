package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import side.onetime.domain.FixedEvent;

import static side.onetime.domain.QFixedSelection.fixedSelection;

@RequiredArgsConstructor
public class FixedSelectionRepositoryImpl implements FixedSelectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 고정 이벤트 기반 고정 선택 스케줄 삭제
    @Override
    public void deleteFixedSelectionsByEvent(FixedEvent fixedEvent) {
        queryFactory.delete(fixedSelection)
                .where(fixedSelection.fixedEvent.eq(fixedEvent))
                .execute();
    }
}