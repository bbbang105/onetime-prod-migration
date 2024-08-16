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
import java.util.*;
import java.util.stream.Collectors;

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

    // 날짜 스케줄 등록 메서드
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

    // 전체 요일 스케줄 반환 메서드
    public List<ScheduleDto.PerDaySchedulesResponse> getAllDaySchedules(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        List<Member> members = memberRepository.findAllWithSelectionsAndSchedulesByEvent(event);

        List<ScheduleDto.PerDaySchedulesResponse> perDaySchedulesResponses = new ArrayList<>();

        for (Member member : members) {
            Map<String, List<Selection>> groupedSelectionsByDay = member.getSelections().stream()
                    .collect(Collectors.groupingBy(
                            selection -> selection.getSchedule().getDay(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            List<ScheduleDto.DaySchedule> daySchedules = groupedSelectionsByDay.entrySet().stream()
                    .map(entry -> ScheduleDto.DaySchedule.of(entry.getValue()))
                    .collect(Collectors.toList());
            perDaySchedulesResponses.add(ScheduleDto.PerDaySchedulesResponse.of(member, daySchedules));
        }
        return perDaySchedulesResponses;
    }

    // 전체 날짜 스케줄 반환 메서드
    public List<ScheduleDto.PerDateSchedulesResponse> getAllDateSchedules(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        List<Member> members = memberRepository.findAllWithSelectionsAndSchedulesByEvent(event);

        List<ScheduleDto.PerDateSchedulesResponse> perDateSchedulesResponses = new ArrayList<>();

        for (Member member : members) {
            Map<String, List<Selection>> groupedSelectionsByDate = member.getSelections().stream()
                    .collect(Collectors.groupingBy(
                            selection -> selection.getSchedule().getDate(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            List<ScheduleDto.DateSchedule> dateSchedules = groupedSelectionsByDate.entrySet().stream()
                    .map(entry -> ScheduleDto.DateSchedule.of(entry.getValue()))
                    .collect(Collectors.toList());
            perDateSchedulesResponses.add(ScheduleDto.PerDateSchedulesResponse.of(member, dateSchedules));
        }
        return perDateSchedulesResponses;
    }
}