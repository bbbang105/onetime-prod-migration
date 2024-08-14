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
@Table(name = "selections")
public class Selection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selections_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_id", foreignKey = @ForeignKey(name = "selections_fk_members_id"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedules_id", foreignKey = @ForeignKey(name = "selections_fk_schedules_id"))
    private Schedule schedule;

    @Builder
    public Selection(Member member, Schedule schedule) {
        this.member = member;
        this.schedule = schedule;
    }
}