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
    @JoinColumn(name = "users_id", foreignKey = @ForeignKey(name = "fixed_selections_fk_users_id"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixed_schedules_id", foreignKey = @ForeignKey(name = "fixed_selections_fk_fixed_schedules_id"))
    private FixedSchedule fixedSchedule;

    @Builder
    public FixedSelection(User user, FixedSchedule fixedSchedule) {
        this.user = user;
        this.fixedSchedule = fixedSchedule;
    }
}
