package side.onetime.repository.custom;

import side.onetime.domain.FixedEvent;
import side.onetime.domain.User;

import java.util.List;

public interface FixedEventRepositoryCustom {
    List<FixedEvent> findAllByUser(User user);
    FixedEvent findByUserAndFixedEventId(User user, Long fixedEventId);
}