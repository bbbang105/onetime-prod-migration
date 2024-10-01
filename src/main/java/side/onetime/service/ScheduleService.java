package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.domain.*;
import side.onetime.dto.ScheduleDto;
import side.onetime.exception.*;
import side.onetime.repository.*;
import side.onetime.util.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final SelectionRepository selectionRepository;
    private final JwtUtil jwtUtil;

    // 요일 스케줄 등록 메서드 (비로그인)
    @Transactional
    public void createDaySchedulesForAnonymousUser(ScheduleDto.CreateDayScheduleRequest createDayScheduleRequest) {
        Event event = eventRepository.findByEventId(UUID.fromString(createDayScheduleRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        Member member = memberRepository.findByMemberId(UUID.fromString(createDayScheduleRequest.getMemberId()))
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        List<ScheduleDto.DaySchedule> daySchedules = createDayScheduleRequest.getDaySchedules();
        List<Selection> selections = new ArrayList<>();
        for (ScheduleDto.DaySchedule daySchedule : daySchedules) {
            String day = daySchedule.getDay();
            List<String> times = daySchedule.getTimes();
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

    // 요일 스케줄 등록 메서드 (로그인)
    @Transactional
    public void createDaySchedulesForAuthenticatedUser(ScheduleDto.CreateDayScheduleRequest createDayScheduleRequest, String authorizationHeader) {
        Event event = eventRepository.findByEventId(UUID.fromString(createDayScheduleRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        List<ScheduleDto.DaySchedule> daySchedules = createDayScheduleRequest.getDaySchedules();
        List<Selection> newSelections = new ArrayList<>();
        List<Schedule> allSchedules = new ArrayList<>();

        for (ScheduleDto.DaySchedule daySchedule : daySchedules) {
            String day = daySchedule.getDay();
            List<String> times = daySchedule.getTimes();
            List<Schedule> schedules = scheduleRepository.findAllByEventAndDay(event, day)
                    .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_DAY_SCHEDULES));

            for (Schedule schedule : schedules) {
                if (times.contains(schedule.getTime())) {
                    newSelections.add(Selection.builder()
                            .user(user)
                            .schedule(schedule)
                            .build());
                }
            }
            allSchedules.addAll(schedules);
        }
        selectionRepository.deleteAllByUserAndScheduleIn(user, allSchedules);
        selectionRepository.saveAll(newSelections);
    }


    // 날짜 스케줄 등록 메서드 (비로그인)
    @Transactional
    public void createDateSchedulesForAnonymousUser(ScheduleDto.CreateDateScheduleRequest createDateScheduleRequest) {
        Event event = eventRepository.findByEventId(UUID.fromString(createDateScheduleRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        Member member = memberRepository.findByMemberId(UUID.fromString(createDateScheduleRequest.getMemberId()))
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        List<ScheduleDto.DateSchedule> dateSchedules = createDateScheduleRequest.getDateSchedules();
        List<Selection> selections = new ArrayList<>();
        for (ScheduleDto.DateSchedule dateSchedule : dateSchedules) {
            String date = dateSchedule.getDate();
            List<String> times = dateSchedule.getTimes();
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

    // 날짜 스케줄 등록 메서드 (로그인)
    @Transactional
    public void createDateSchedulesForAuthenticatedUser(ScheduleDto.CreateDateScheduleRequest createDateScheduleRequest, String authorizationHeader) {
        Event event = eventRepository.findByEventId(UUID.fromString(createDateScheduleRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        List<ScheduleDto.DateSchedule> dateSchedules = createDateScheduleRequest.getDateSchedules();
        List<Selection> newSelections = new ArrayList<>();
        List<Schedule> allSchedules = new ArrayList<>();

        for (ScheduleDto.DateSchedule dateSchedule : dateSchedules) {
            String date = dateSchedule.getDate();
            List<String> times = dateSchedule.getTimes();

            List<Schedule> schedules = scheduleRepository.findAllByEventAndDate(event, date)
                    .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_DATE_SCHEDULES));

            for (Schedule schedule : schedules) {
                if (times.contains(schedule.getTime())) {
                    newSelections.add(Selection.builder()
                            .user(user)
                            .schedule(schedule)
                            .build());
                }
            }

            allSchedules.addAll(schedules);
        }

        selectionRepository.deleteAllByUserAndScheduleIn(user, allSchedules);
        selectionRepository.saveAll(newSelections);
    }

    // 전체 요일 스케줄 반환 메서드
    @Transactional
    public List<ScheduleDto.PerDaySchedulesResponse> getAllDaySchedules(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        // 이벤트에 참여하는 모든 멤버
        List<Member> members = memberRepository.findAllWithSelectionsAndSchedulesByEvent(event);
        // 이벤트에 참여하는 모든 유저
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEvent(event);
        List<User> users = eventParticipations.stream()
                .map(EventParticipation::getUser)
                .toList();

        List<ScheduleDto.PerDaySchedulesResponse> perDaySchedulesResponses = new ArrayList<>();

        // 멤버 스케줄 추가
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
            perDaySchedulesResponses.add(ScheduleDto.PerDaySchedulesResponse.of(member.getName(), daySchedules));
        }

        // 유저 스케줄 추가
        for (User user : users) {
            Map<String, List<Selection>> groupedSelectionsByDay = user.getSelections().stream()
                    .filter(selection -> selection.getSchedule().getEvent().equals(event))
                    .collect(Collectors.groupingBy(
                            selection -> selection.getSchedule().getDay(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            List<ScheduleDto.DaySchedule> daySchedules = groupedSelectionsByDay.entrySet().stream()
                    .map(entry -> ScheduleDto.DaySchedule.of(entry.getValue()))
                    .collect(Collectors.toList());
            perDaySchedulesResponses.add(ScheduleDto.PerDaySchedulesResponse.of(user.getNickname(), daySchedules));
        }

        return perDaySchedulesResponses;
    }

    // 개인 요일 스케줄 반환 메서드
    @Transactional
    public ScheduleDto.PerDaySchedulesResponse getMemberDaySchedules(String eventId, String memberId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        Member member = memberRepository.findByMemberIdWithSelections(UUID.fromString(memberId))
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        Map<String, List<Selection>> groupedSelectionsByDay = member.getSelections().stream()
                .collect(Collectors.groupingBy(
                        selection -> selection.getSchedule().getDay(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ScheduleDto.DaySchedule> daySchedules = groupedSelectionsByDay.entrySet().stream()
                .map(entry -> ScheduleDto.DaySchedule.of(entry.getValue()))
                .collect(Collectors.toList());

        return ScheduleDto.PerDaySchedulesResponse.of(member.getName(), daySchedules);
    }

    // 전체 날짜 스케줄 반환 메서드
    @Transactional
    public List<ScheduleDto.PerDateSchedulesResponse> getAllDateSchedules(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        // 이벤트에 참여하는 모든 멤버
        List<Member> members = memberRepository.findAllWithSelectionsAndSchedulesByEvent(event);
        // 이벤트에 참여하는 모든 유저
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEvent(event);
        List<User> users = eventParticipations.stream()
                .map(EventParticipation::getUser)
                .toList();

        List<ScheduleDto.PerDateSchedulesResponse> perDateSchedulesResponses = new ArrayList<>();

        // 멤버 스케줄 추가
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
            perDateSchedulesResponses.add(ScheduleDto.PerDateSchedulesResponse.of(member.getName(), dateSchedules));
        }

        // 유저 스케줄 추가
        for (User user : users) {
            Map<String, List<Selection>> groupedSelectionsByDate = user.getSelections().stream()
                    .filter(selection -> selection.getSchedule().getEvent().equals(event))
                    .collect(Collectors.groupingBy(
                            selection -> selection.getSchedule().getDate(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            List<ScheduleDto.DateSchedule> dateSchedules = groupedSelectionsByDate.entrySet().stream()
                    .map(entry -> ScheduleDto.DateSchedule.of(entry.getValue()))
                    .collect(Collectors.toList());
            perDateSchedulesResponses.add(ScheduleDto.PerDateSchedulesResponse.of(user.getNickname(), dateSchedules));
        }

        return perDateSchedulesResponses;
    }

    // 개인 날짜 스케줄 반환 메서드
    @Transactional
    public ScheduleDto.PerDateSchedulesResponse getMemberDateSchedules(String eventId, String memberId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        Member member = memberRepository.findByMemberIdWithSelections(UUID.fromString(memberId))
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        Map<String, List<Selection>> groupedSelectionsByDate = member.getSelections().stream()
                .collect(Collectors.groupingBy(
                        selection -> selection.getSchedule().getDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ScheduleDto.DateSchedule> dateSchedules = groupedSelectionsByDate.entrySet().stream()
                .map(entry -> ScheduleDto.DateSchedule.of(entry.getValue()))
                .collect(Collectors.toList());

        return ScheduleDto.PerDateSchedulesResponse.of(member.getName(), dateSchedules);
    }

    // 멤버 필터링 요일 스케줄 반환 메서드
    @Transactional
    public List<ScheduleDto.PerDaySchedulesResponse> getFilteredDaySchedules(ScheduleDto.GetFilteredSchedulesRequest getFilteredSchedulesRequest) {
        Event event = eventRepository.findByEventId(UUID.fromString(getFilteredSchedulesRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        List<Member> members = memberRepository.findAllWithSelectionsAndSchedulesByEventAndNames(event, getFilteredSchedulesRequest.getNames());

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
            perDaySchedulesResponses.add(ScheduleDto.PerDaySchedulesResponse.of(member.getName(), daySchedules));
        }
        return perDaySchedulesResponses;
    }

    // 멤버 필터링 날짜 스케줄 반환 메서드
    @Transactional
    public List<ScheduleDto.PerDateSchedulesResponse> getFilteredDateSchedules(ScheduleDto.GetFilteredSchedulesRequest getFilteredSchedulesRequest) {
        Event event = eventRepository.findByEventId(UUID.fromString(getFilteredSchedulesRequest.getEventId()))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        List<Member> members = memberRepository.findAllWithSelectionsAndSchedulesByEventAndNames(event, getFilteredSchedulesRequest.getNames());

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
            perDateSchedulesResponses.add(ScheduleDto.PerDateSchedulesResponse.of(member.getName(), dateSchedules));
        }
        return perDateSchedulesResponses;
    }
}