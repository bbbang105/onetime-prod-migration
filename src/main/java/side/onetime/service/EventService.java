package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.domain.Schedule;
import side.onetime.dto.EventDto;
import side.onetime.exception.EventErrorResult;
import side.onetime.exception.EventException;
import side.onetime.exception.ScheduleErrorResult;
import side.onetime.exception.ScheduleException;
import side.onetime.global.common.constant.Category;
import side.onetime.repository.EventRepository;
import side.onetime.repository.ScheduleRepository;
import side.onetime.util.DateUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    protected void createAndSaveDateSchedules(Event event, List<String> ranges, String startTime, String endTime) {
        List<LocalTime> timeSets = dateUtil.createTimeSets(startTime, endTime);
        List<Schedule> schedules = new ArrayList<>();
        for (String range : ranges) {
            for (LocalTime time : timeSets) {
                Schedule schedule = Schedule.builder()
                        .event(event)
                        .date(range)
                        .time(String.valueOf(time))
                        .build();
                schedules.add(schedule);
            }
        }
        scheduleRepository.saveAll(schedules);
    }

    // 요일 스케줄을 생성하고 저장하는 메서드
    @Transactional
    protected void createAndSaveDaySchedules(Event event, List<String> ranges, String startTime, String endTime) {
        List<LocalTime> timeSets = dateUtil.createTimeSets(startTime, endTime);
        List<Schedule> schedules = new ArrayList<>();
        for (String range : ranges) {
            for (LocalTime time : timeSets) {
                Schedule schedule = Schedule.builder()
                        .event(event)
                        .day(range)
                        .time(String.valueOf(time))
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

        List<Schedule> schedules = scheduleRepository.findAllByEvent(event)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_ALL_SCHEDULES));

        List<String> ranges;
        if (event.getCategory().equals(Category.DATE)) {
            ranges = getDateRanges(schedules);
        } else {
            ranges = getDayRanges(schedules);
        }

        return EventDto.GetEventResponse.of(event, ranges);
    }

    private List<String> getDateRanges(List<Schedule> schedules) {
        return schedules.stream()
                .map(Schedule::getDate)
                .filter(date -> date != null && !date.isEmpty())
                .distinct()
                .toList();
    }

    private List<String> getDayRanges(List<Schedule> schedules) {
        return schedules.stream()
                .map(Schedule::getDay)
                .filter(day -> day != null && !day.isEmpty())
                .distinct()
                .toList();
    }

    // 참여자 조회 메서드
    @Transactional
    public EventDto.GetParticipantsResponse getParticipants(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        List<Member> members = event.getMembers();

        return EventDto.GetParticipantsResponse.of(members);
    }
}