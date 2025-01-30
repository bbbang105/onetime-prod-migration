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
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_id", nullable = false, length = 50, unique = true)
    private String providerId;

    @Column(name = "service_policy_agreement")
    private Boolean servicePolicyAgreement;

    @Column(name = "privacy_policy_agreement")
    private Boolean privacyPolicyAgreement;

    @Column(name = "marketing_policy_agreement")
    private Boolean marketingPolicyAgreement;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Selection> selections;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventParticipation> eventParticipations;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FixedEvent> fixedEvents;

    @Builder
    public User(String name, String email, String nickname, String provider, String providerId, Boolean servicePolicyAgreement, Boolean privacyPolicyAgreement, Boolean marketingPolicyAgreement) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.servicePolicyAgreement = servicePolicyAgreement;
        this.privacyPolicyAgreement = privacyPolicyAgreement;
        this.marketingPolicyAgreement = marketingPolicyAgreement;
    }

    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }
}
