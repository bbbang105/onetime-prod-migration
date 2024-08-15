package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import side.onetime.domain.Event;
import side.onetime.domain.Schedule;
import side.onetime.dto.EventDto;
import side.onetime.exception.EventErrorResult;
import side.onetime.exception.EventException;
import side.onetime.global.common.constant.Category;
import side.onetime.repository.EventRepository;
import side.onetime.repository.ScheduleRepository;
import side.onetime.util.DateUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ScheduleRepository scheduleRepository;
    private final DateUtil dateUtil;

    // 이벤트 생성 메서드
    @Transactional
    public EventDto.CreateEventResponse createEvent(EventDto.CreateEventRequest createEventRequest) {
        Event event = createEventRequest.to();
        eventRepository.save(event);

        if (createEventRequest.getCategory().equals(Category.DATE)) {
            createAndSaveDateSchedules(event, createEventRequest.getRanges(), createEventRequest.getStartTime(), createEventRequest.getEndTime());
        } else {
            createAndSaveDaySchedules(event, createEventRequest.getRanges(), createEventRequest.getStartTime(), createEventRequest.getEndTime());
        }

        return EventDto.CreateEventResponse.of(event);
    }

    // 날짜 스케줄을 생성하고 저장하는 메서드
    @Transactional
    protected void createAndSaveDateSchedules(Event event, List<String> ranges, LocalTime startTime, LocalTime endTime) {
        List<LocalTime> timeSets = dateUtil.createTimeSets(startTime, endTime);
        List<Schedule> schedules = new ArrayList<>();
        for (String range : ranges) {
            for (LocalTime time : timeSets) {
                Schedule schedule = Schedule.builder()
                        .event(event)
                        .date(range)
                        .time(time)
                        .build();
                schedules.add(schedule);
            }
        }
        scheduleRepository.saveAll(schedules);
    }

    // 요일 스케줄을 생성하고 저장하는 메서드
    @Transactional
    protected void createAndSaveDaySchedules(Event event, List<String> ranges, LocalTime startTime, LocalTime endTime) {
        List<LocalTime> timeSets = dateUtil.createTimeSets(startTime, endTime);
        List<Schedule> schedules = new ArrayList<>();
        for (String range : ranges) {
            for (LocalTime time : timeSets) {
                Schedule schedule = Schedule.builder()
                        .event(event)
                        .day(range)
                        .time(time)
                        .build();
                schedules.add(schedule);
            }
        }
        scheduleRepository.saveAll(schedules);
    }

    // 이벤트 조회 메서드
    public EventDto.GetEventResponse getEvent(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        return EventDto.GetEventResponse.of(event);
    }
}
