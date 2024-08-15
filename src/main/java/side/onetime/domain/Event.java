package side.onetime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.global.common.constant.Category;
import side.onetime.global.common.dao.BaseEntity;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "events")
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "events_id")
    private Long id;

    @Column(name = "events_uuid", columnDefinition = "BINARY(16)", unique = true)
    private UUID eventId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Member> members;

    @Builder
    public Event(UUID eventId, String title, LocalTime startTime, LocalTime endTime, Category category) {
        this.eventId = eventId;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
    }
}