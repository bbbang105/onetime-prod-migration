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
@Table(name = "banners")
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banners_id")
    private Long id;

    @Column(name = "content_kor", nullable = false, length = 200)
    private String contentKor;

    @Column(name = "content_eng", nullable = false, length = 200)
    private String contentEng;

    @Column(name = "background_color_code", nullable = false, length = 30)
    private String backgroundColorCode;

    @Column(name = "text_color_code", nullable = false, length = 30)
    private String textColorCode;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Builder
    public Banner(String contentKor, String contentEng, String backgroundColorCode, String textColorCode) {
        this.contentKor = contentKor;
        this.contentEng = contentEng;
        this.backgroundColorCode = backgroundColorCode;
        this.textColorCode = textColorCode;
        this.isActivated = false;
        this.isDeleted = false;
    }

    public void updateContentKor(String contentKor) {
        this.contentKor = contentKor;
    }

    public void updateContentEng(String contentEng) {
        this.contentEng = contentEng;
    }

    public void updateBackgroundColorCode(String backgroundColorCode) {
        this.backgroundColorCode = backgroundColorCode;
    }

    public void updateTextColorCode(String textColorCode) {
        this.textColorCode = textColorCode;
    }

    public void updateIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
