package side.onetime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.global.common.dao.BaseEntity;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "fixed_schedules")
public class FixedSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fixed_schedules_id")
    private Long id;

    @Column(name = "day", length = 10)
    private String day;

    @Column(name = "time", nullable = false)
    private String time;

    @OneToMany(mappedBy = "fixedSchedule",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixedSelection> fixedSelections;

    @Builder
    public FixedSchedule(String day, String time) {
        this.day = day;
        this.time = time;
    }
}
