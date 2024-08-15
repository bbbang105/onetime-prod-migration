package side.onetime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.global.common.dao.BaseEntity;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "members")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "members_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id", foreignKey = @ForeignKey(name = "members_fk_events_id"))
    private Event event;

    @Column(name = "members_uuid", columnDefinition = "BINARY(16)", unique = true)
    private UUID memberId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pin", nullable = false)
    private String pin;

    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Selection> selections;

    @Builder
    public Member(Event event, UUID memberId, String name, String pin) {
        this.event = event;
        this.memberId = memberId;
        this.name = name;
        this.pin = pin;
    }
}