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

    @OneToMany(mappedBy = "fixedEvent",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixedSelection> fixedSelections;

    @Builder
    public FixedEvent(User user, String title) {
        this.user = user;
        this.title = title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
