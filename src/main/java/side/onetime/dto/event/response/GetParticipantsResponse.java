package side.onetime.dto.event.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import side.onetime.domain.Member;
import side.onetime.domain.User;

import java.util.List;
import java.util.stream.Collectors;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetParticipantsResponse(
        List<String> names
) {
    public static GetParticipantsResponse of(List<Member> members, List<User> users) {
        List<String> names = members.stream().map(Member::getName).collect(Collectors.toList());
        names.addAll(users.stream().map(User::getNickname).collect(Collectors.toList()));
        return new GetParticipantsResponse(names);
    }
}
