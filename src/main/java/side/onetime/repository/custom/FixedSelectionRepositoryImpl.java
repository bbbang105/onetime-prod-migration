package side.onetime.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import side.onetime.domain.FixedSelection;
import side.onetime.domain.User;

import java.util.List;

import static side.onetime.domain.QFixedSchedule.fixedSchedule;
import static side.onetime.domain.QFixedSelection.fixedSelection;

@RequiredArgsConstructor
public class FixedSelectionRepositoryImpl implements FixedSelectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 유저 기반 고정 스케줄 조회 메서드.
     *
     * 특정 유저의 모든 고정 선택 및 관련된 고정 스케줄을 조회합니다.
     *
     * @param user 조회할 유저 객체
     * @return 유저의 고정 선택 리스트
     */
    @Override
    public List<FixedSelection> findAllByUser(User user) {
        return queryFactory.selectFrom(fixedSelection)
                .leftJoin(fixedSelection.fixedSchedule, fixedSchedule)
                .fetchJoin()
                .where(fixedSelection.user.eq(user))
                .fetch();
    }

    /**
     * 유저 기반 고정 선택 스케줄 삭제 메서드.
     *
     * 주어진 유저에 연결된 모든 고정 선택 데이터를 삭제합니다.
     * @param user 삭제할 고정 선택 데이터의 기준이 되는 유저 객체
     */
    @Override
    public void deleteFixedSelectionsByUser(User user) {
        queryFactory.delete(fixedSelection)
                .where(fixedSelection.user.eq(user))
                .execute();
    }
}
