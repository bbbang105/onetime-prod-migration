package side.onetime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import side.onetime.global.common.dao.BaseEntity;

import java.time.LocalTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "schedules")
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedules_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_id", foreignKey = @ForeignKey(name = "schedules_fk_events_id"))
    private Event event;

    @Column(name = "date")
    private String date;

    @Column(name = "day", length = 10)
    private String day;

    @Column(name = "time", nullable = false)
    private String time;

    @OneToMany(mappedBy = "schedule",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private List<Selection> selections;

    @Builder
    public Schedule(Event event, String date, String day, String time) {
        this.event = event;
        this.date = date;
        this.day = day;
        this.time = time;
    }
}