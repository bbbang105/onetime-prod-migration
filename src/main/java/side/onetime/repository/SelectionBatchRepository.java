package side.onetime.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import side.onetime.domain.Member;
import side.onetime.domain.Schedule;
import side.onetime.domain.Selection;
import side.onetime.domain.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SelectionBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void insertAll(List<Selection> selections) {
        String sql = "INSERT INTO selections (members_id, users_id, schedules_id, created_date, updated_date) VALUES (?, ?, ?, ?, ?)";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Selection selection = selections.get(i);
                ps.setObject(1, Optional.ofNullable(selection.getMember())
                        .map(Member::getId)
                        .orElse(null), java.sql.Types.BIGINT);
                ps.setObject(2, Optional.ofNullable(selection.getUser())
                        .map(User::getId)
                        .orElse(null), java.sql.Types.BIGINT);
                ps.setObject(3, Optional.ofNullable(selection.getSchedule())
                        .map(Schedule::getId)
                        .orElse(null), java.sql.Types.BIGINT);
                ps.setTimestamp(4, now);
                ps.setTimestamp(5, now);
            }

            @Override
            public int getBatchSize() {
                return selections.size();
            }
        });
    }
}
