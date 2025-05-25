package side.onetime.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;
import side.onetime.domain.Schedule;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void insertAll(List<Schedule> schedules) {
        String sql = "INSERT INTO schedules (events_id, date, day, time, created_date, updated_date) VALUES (?, ?, ?, ?, ?, ?)";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Schedule schedule = schedules.get(i);
                ps.setLong(1, schedule.getEvent().getId());
                ps.setString(2, schedule.getDate());
                ps.setString(3, schedule.getDay());
                ps.setString(4, schedule.getTime());
                ps.setTimestamp(5, now);
                ps.setTimestamp(6, now);
            }

            @Override
            public int getBatchSize() {
                return schedules.size();
            }
        });
    }
}
