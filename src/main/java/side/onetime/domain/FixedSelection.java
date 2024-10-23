package side.onetime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.global.common.dao.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "fixed_selections")
public class FixedSelection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fixed_selections_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_events_id", foreignKey = @ForeignKey(name = "fixed_selections_fk_fixed_events_id"))
    private FixedEvent fixedEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_schedules_id", foreignKey = @ForeignKey(name = "fixed_selections_fk_fixed_schedules_id"))
    private FixedSchedule fixedSchedule;

    @Builder
    public FixedSelection(FixedEvent fixedEvent, FixedSchedule fixedSchedule) {
        this.fixedEvent = fixedEvent;
        this.fixedSchedule = fixedSchedule;
    }
}