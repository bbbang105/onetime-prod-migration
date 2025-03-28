package side.onetime.repository.custom;

import org.springframework.data.domain.Pageable;
import side.onetime.domain.Event;

import java.util.List;

public interface EventRepositoryCustom {

    void deleteEvent(Event event);

    void deleteSchedulesByRange(Event event, String range);

    void deleteSchedulesByTime(Event event, String time);

    List<Event> findAllWithSort(Pageable pageable, String keyword, String sorting);
}
