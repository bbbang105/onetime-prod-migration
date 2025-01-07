package side.onetime.repository.custom;

import side.onetime.domain.Event;

public interface EventRepositoryCustom {
    void deleteEvent(Event event);
    void deleteSchedulesByRange(Event event, String range);
    void deleteSchedulesByTime(Event event, String time);
}
