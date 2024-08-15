package side.onetime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import side.onetime.domain.Event;
import side.onetime.domain.Member;

import java.util.UUID;

public class MemberDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginMemberRequest {
        private String eventId;
        private String name;
        private String pin;

        public Member to(Event event) {
            return Member.builder()
                    .event(event)
                    .memberId(UUID.randomUUID())
                    .name(name)
                    .pin(pin)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LoginMemberResponse {
        private String memberId;

        public static MemberDto.LoginMemberResponse of(Member member) {
            return MemberDto.LoginMemberResponse.builder()
                    .memberId(String.valueOf(member.getMemberId()))
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsDuplicateRequest {
        private String eventId;
        private String name;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsDuplicateResponse {
        private Boolean isPossible;

        public static MemberDto.IsDuplicateResponse of(Boolean isPossible) {
            return MemberDto.IsDuplicateResponse.builder()
                    .isPossible(isPossible)
                    .build();
        }
    }
}