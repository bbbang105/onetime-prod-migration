package side.onetime.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.global.common.dao.BaseEntity;

import java.time.LocalDateTime;

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
    @JoinColumn(name = "members_id", foreignKey = @ForeignKey(name = "schedules_fk_members_id"))
    private Member member;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "day", nullable = false, length = 10)
    private String day;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected;

    @Builder
    public Schedule(Member member, LocalDateTime date, String day, LocalDateTime time) {
        this.member = member;
        this.date = date;
        this.day = day;
        this.time = time;
        this.isSelected = false;
    }

    public void updateIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}