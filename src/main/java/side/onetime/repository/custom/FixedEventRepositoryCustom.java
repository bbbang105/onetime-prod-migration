package side.onetime.repository.custom;

import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;

import java.util.List;

public interface FixedEventRepositoryCustom {
    List<FixedEvent> findAllByUser(User user);
    FixedEvent findByUserAndFixedEventIdCustom(User user, Long fixedEventId);
    void deleteFixedEventAndSelections(User user, Long fixedEventId);
}