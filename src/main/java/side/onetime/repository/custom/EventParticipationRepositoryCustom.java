package side.onetime.repository.custom;

import java.util.List;
import java.util.Map;

public interface EventParticipationRepositoryCustom {
    Map<Long, Integer> countParticipantsByEventIds(List<Long> eventIds);
}
