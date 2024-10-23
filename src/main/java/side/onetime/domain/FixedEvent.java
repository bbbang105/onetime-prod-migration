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
@Table(name = "fixed_events")
public class FixedEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fixed_events_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", foreignKey = @ForeignKey(name = "fixed_events_fk_users_id"))
    private User user;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @OneToMany(mappedBy = "fixedEvent",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixedSelection> fixedSelections;

    @Builder
    public FixedEvent(User user, String title, String startTime, String endTime) {
        this.user = user;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(String endTime) {
        this.endTime = endTime;
    }
}