package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.domain.*;
import side.onetime.domain.enums.EventStatus;
import side.onetime.dto.EventDto;
import side.onetime.exception.EventErrorResult;
import side.onetime.exception.EventException;
import side.onetime.exception.ScheduleErrorResult;
import side.onetime.exception.ScheduleException;
import side.onetime.global.common.constant.Category;
import side.onetime.repository.*;
import side.onetime.util.DateUtil;
import side.onetime.util.JwtUtil;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private static final int MAX_MOST_POSSIBLE_TIMES_SIZE = 6;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final ScheduleRepository scheduleRepository;
    private final SelectionRepository selectionRepository;
    private final JwtUtil jwtUtil;

    // 이벤트 생성 메서드 (비로그인)
    @Transactional
    public EventDto.CreateEventResponse createEventForAnonymousUser(EventDto.CreateEventRequest createEventRequest) {
        Event event = createEventRequest.to();
        eventRepository.save(event);

        if (createEventRequest.getCategory().equals(Category.DATE)) {
            if (!isDateFormat(createEventRequest.getRanges().get(0))) {
                throw new EventException(EventErrorResult._IS_NOT_DATE_FORMAT);
            }
            createAndSaveDateSchedules(event, createEventRequest.getRanges(), createEventRequest.getStartTime(), createEventRequest.getEndTime());
        } else {
            if (isDateFormat(createEventRequest.getRanges().get(0))) {
                throw new EventException(EventErrorResult._IS_NOT_DAY_FORMAT);
            }
            createAndSaveDaySchedules(event, createEventRequest.getRanges(), createEventRequest.getStartTime(), createEventRequest.getEndTime());
        }

        return EventDto.CreateEventResponse.of(event);
    }

    // 이벤트 생성 메서드 (로그인)
    @Transactional
    public EventDto.CreateEventResponse createEventForAuthenticatedUser(EventDto.CreateEventRequest createEventRequest, String authorizationHeader) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Event event = createEventRequest.to();
        EventParticipation eventParticipation = EventParticipation.builder()
                .user(user)
                .event(event)
                .eventStatus(EventStatus.CREATOR)
                .build();
        eventRepository.save(event);
        eventParticipationRepository.save(eventParticipation);

        if (createEventRequest.getCategory().equals(Category.DATE)) {
            if (!isDateFormat(createEventRequest.getRanges().get(0))) {
                throw new EventException(EventErrorResult._IS_NOT_DATE_FORMAT);
            }
            createAndSaveDateSchedules(event, createEventRequest.getRanges(), createEventRequest.getStartTime(), createEventRequest.getEndTime());
        } else {
            if (isDateFormat(createEventRequest.getRanges().get(0))) {
                throw new EventException(EventErrorResult._IS_NOT_DAY_FORMAT);
            }
            createAndSaveDaySchedules(event, createEventRequest.getRanges(), createEventRequest.getStartTime(), createEventRequest.getEndTime());
        }

        return EventDto.CreateEventResponse.of(event);
    }

    // 날짜 스케줄을 생성하고 저장하는 메서드
    @Transactional
    protected void createAndSaveDateSchedules(Event event, List<String> ranges, String startTime, String endTime) {
        List<LocalTime> timeSets = DateUtil.createTimeSets(startTime, endTime);
        List<Schedule> schedules = ranges.stream()
                .flatMap(range -> timeSets.stream()
                        .map(time -> Schedule.builder()
                                .event(event)
                                .date(range)
                                .time(String.valueOf(time))
                                .build()))
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }

    // 요일 스케줄을 생성하고 저장하는 메서드
    @Transactional
    protected void createAndSaveDaySchedules(Event event, List<String> ranges, String startTime, String endTime) {
        List<LocalTime> timeSets = DateUtil.createTimeSets(startTime, endTime);
        List<Schedule> schedules = ranges.stream()
                .flatMap(range -> timeSets.stream()
                        .map(time -> Schedule.builder()
                                .event(event)
                                .day(range)
                                .time(String.valueOf(time))
                                .build()))
                .collect(Collectors.toList());
        scheduleRepository.saveAll(schedules);
    }

    // 이벤트 조회 메서드
    public EventDto.GetEventResponse getEvent(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        List<Schedule> schedules = scheduleRepository.findAllByEvent(event)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_ALL_SCHEDULES));

        List<String> ranges = event.getCategory().equals(Category.DATE)
                ? DateUtil.getSortedDateRanges(schedules.stream().map(Schedule::getDate).toList(), "yyyy.MM.dd")
                : DateUtil.getSortedDayRanges(schedules.stream().map(Schedule::getDay).toList());

        return EventDto.GetEventResponse.of(event, ranges);
    }

    // 참여자 조회 메서드
    @Transactional
    public EventDto.GetParticipantsResponse getParticipants(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        // 이벤트에 참여하는 모든 멤버
        List<Member> members = event.getMembers();
        // 이벤트에 참여하는 모든 유저
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEvent(event);
        List<User> users = eventParticipations.stream()
                .filter(eventParticipation -> {
                    // CREATOR일 경우, 스케줄 등록을 했는지 확인
                    if (eventParticipation.getEventStatus() == EventStatus.CREATOR) {
                        return selectionRepository.existsByUserAndEventSchedules(eventParticipation.getUser(), eventParticipation.getEvent());
                    }
                    return true;
                })
                .map(EventParticipation::getUser)
                .toList();

        return EventDto.GetParticipantsResponse.of(members, users);
    }

    // 가장 많이 되는 시간 조회 메서드
    @Transactional
    public List<EventDto.GetMostPossibleTime> getMostPossibleTime(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        // 이벤트에 참여하는 모든 멤버
        List<Member> members = event.getMembers();
        List<String> allMembersName = members.stream()
                .map(Member::getName)
                .toList();

        // 이벤트에 참여하는 모든 유저
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEvent(event);

        EventDto.GetParticipantsResponse getParticipantsResponse = getParticipants(eventId);
        List<String> participantNames = getParticipantsResponse.getNames();

        // 유저 필터링: 참여자 목록에 있는 유저만 포함
        List<String> allUserNicknames = eventParticipations.stream()
                .map(EventParticipation::getUser)
                .map(User::getNickname)
                .filter(participantNames::contains)
                .toList();

        List<Selection> selections = selectionRepository.findAllSelectionsByEvent(event);

        // 스케줄과 선택된 참여자 이름 매핑
        Map<Schedule, List<String>> scheduleToNamesMap = buildScheduleToNamesMap(selections);

        int mostPossibleCnt = scheduleToNamesMap.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        // 멤버와 유저 전체 이름 합치기
        List<String> allParticipants = new ArrayList<>(allMembersName);
        allParticipants.addAll(allUserNicknames);

        List<EventDto.GetMostPossibleTime> mostPossibleTimes = buildMostPossibleTimes(scheduleToNamesMap, mostPossibleCnt, allParticipants, event.getCategory());

        return DateUtil.sortMostPossibleTimes(mostPossibleTimes, event.getCategory());
    }

    // 스케줄과 선택된 참여자 이름 매핑 (멤버 이름 / 유저 닉네임)
    private Map<Schedule, List<String>> buildScheduleToNamesMap(List<Selection> selections) {
        return selections.stream()
                .collect(Collectors.groupingBy(
                        Selection::getSchedule,
                        LinkedHashMap::new,
                        Collectors.mapping(selection -> {
                            if (selection.getMember() != null) {
                                return selection.getMember().getName();
                            } else if (selection.getUser() != null) {
                                return selection.getUser().getNickname();
                            }
                            return null;
                        }, Collectors.toList())
                ));
    }

    // 최적 시간대 리스트 생성
    private List<EventDto.GetMostPossibleTime> buildMostPossibleTimes(Map<Schedule, List<String>> scheduleToNamesMap, int mostPossibleCnt, List<String> allMembersName, Category category) {
        List<EventDto.GetMostPossibleTime> mostPossibleTimes = new ArrayList<>();
        EventDto.GetMostPossibleTime previousTime = null;

        for (Map.Entry<Schedule, List<String>> entry : scheduleToNamesMap.entrySet()) {
            Schedule schedule = entry.getKey();
            List<String> curNames = entry.getValue();

            if (curNames.size() == mostPossibleCnt) {
                if (canMergeWithPrevious(previousTime, schedule, curNames, category)) {
                    previousTime.updateEndTime(schedule.getTime());
                } else {
                    List<String> impossibleNames = allMembersName.stream()
                            .filter(name -> !curNames.contains(name))
                            .toList();

                    EventDto.GetMostPossibleTime newTime = createMostPossibleTime(schedule, curNames, impossibleNames, category);
                    mostPossibleTimes.add(newTime);
                    previousTime = newTime;
                }
            }

            if (mostPossibleTimes.size() == MAX_MOST_POSSIBLE_TIMES_SIZE) {
                break;
            }
        }
        return mostPossibleTimes;
    }

    // 이전 시간대와 병합이 가능한지 확인
    private boolean canMergeWithPrevious(EventDto.GetMostPossibleTime previousTime, Schedule schedule, List<String> curNames, Category category) {
        if (previousTime == null) return false;

        boolean isSameTimePoint = category.equals(Category.DAY)
                ? previousTime.getTimePoint().equals(schedule.getDay())
                : previousTime.getTimePoint().equals(schedule.getDate());

        return isSameTimePoint
                && previousTime.getEndTime().equals(schedule.getTime())
                && new HashSet<>(previousTime.getPossibleNames()).containsAll(curNames);
    }

    // 새로운 시간대 객체 생성
    private EventDto.GetMostPossibleTime createMostPossibleTime(Schedule schedule, List<String> curNames, List<String> impossibleNames, Category category) {
        return category.equals(Category.DAY)
                ? EventDto.GetMostPossibleTime.dayOf(schedule, curNames, impossibleNames)
                : EventDto.GetMostPossibleTime.dateOf(schedule, curNames, impossibleNames);
    }

    // 날짜 포맷인지 검증
    private boolean isDateFormat(String range) {
        return Character.isDigit(range.charAt(0));
    }

    // 유저 참여 이벤트 반환 메서드
    @Transactional
    public List<EventDto.GetUserParticipatedEventsResponse> getUserParticipatedEvents(String authorizationHeader) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        return eventParticipationRepository.findAllByUser(user).stream()
                .sorted(Comparator.comparing(
                                (EventParticipation eventParticipation) -> eventParticipation.getEvent().getCreatedDate())
                        .reversed()) // 최신순으로 정렬
                .map(eventParticipation -> {
                    Event event = eventParticipation.getEvent();
                    String eventId = String.valueOf(event.getEventId());
                    EventDto.GetParticipantsResponse getParticipantsResponse = getParticipants(eventId);
                    List<String> participantNames = getParticipantsResponse.getNames();
                    List<EventDto.GetMostPossibleTime> mostPossibleTimes = getMostPossibleTime(eventId);
                    return EventDto.GetUserParticipatedEventsResponse.of(event, eventParticipation, participantNames.size(), mostPossibleTimes);
                })
                .collect(Collectors.toList());
    }
}