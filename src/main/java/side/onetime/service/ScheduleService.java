package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.domain.Schedule;
import side.onetime.domain.Selection;
import side.onetime.dto.ScheduleDto;
import side.onetime.exception.*;
import side.onetime.repository.EventRepository;
import side.onetime.repository.MemberRepository;
import side.onetime.repository.ScheduleRepository;
import side.onetime.repository.SelectionRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final SelectionRepository selectionRepository;

    // 요일 스케줄 등록 메서드
    @Transactional
    public void createDaySchedules(ScheduleDto.CreateDayScheduleRequest createDayScheduleRequest) {
        Event event = eventRepository.findByEventId(UUID.fromString(createDayScheduleRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        Member member = memberRepository.findByMemberId(UUID.fromString(createDayScheduleRequest.getMemberId()))
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        List<ScheduleDto.DaySchedule> daySchedules = createDayScheduleRequest.getDaySchedules();
        List<Selection> selections = new ArrayList<>();
        for (ScheduleDto.DaySchedule daySchedule : daySchedules) {
            String day = daySchedule.getDay();
            List<LocalTime> times = daySchedule.getTimes();
            List<Schedule> schedules = scheduleRepository.findAllByEventAndDay(event, day)
                    .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_DAY_SCHEDULES));

            for (Schedule schedule : schedules) {
                if (times.contains(schedule.getTime())) {
                    selections.add(Selection.builder()
                            .member(member)
                            .schedule(schedule)
                            .build());
                }
            }
        }
        selectionRepository.deleteAllByMember(member);
        selectionRepository.flush();
        selectionRepository.saveAll(selections);
    }

    // 요일 스케줄 등록 메서드
    @Transactional
    public void createDateSchedules(ScheduleDto.CreateDateScheduleRequest createDateScheduleRequest) {
        Event event = eventRepository.findByEventId(UUID.fromString(createDateScheduleRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        Member member = memberRepository.findByMemberId(UUID.fromString(createDateScheduleRequest.getMemberId()))
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        List<ScheduleDto.DateSchedule> dateSchedules = createDateScheduleRequest.getDateSchedules();
        List<Selection> selections = new ArrayList<>();
        for (ScheduleDto.DateSchedule dateSchedule : dateSchedules) {
            String date = dateSchedule.getDate();
            List<LocalTime> times = dateSchedule.getTimes();
            List<Schedule> schedules = scheduleRepository.findAllByEventAndDate(event, date)
                    .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_DATE_SCHEDULES));

            for (Schedule schedule : schedules) {
                if (times.contains(schedule.getTime())) {
                    selections.add(Selection.builder()
                            .member(member)
                            .schedule(schedule)
                            .build());
                }
            }
        }
        selectionRepository.deleteAllByMember(member);
        selectionRepository.flush();
        selectionRepository.saveAll(selections);
    }
}