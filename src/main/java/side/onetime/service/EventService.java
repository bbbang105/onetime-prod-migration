package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import side.onetime.domain.*;
import side.onetime.domain.enums.Category;
import side.onetime.domain.enums.EventStatus;
import side.onetime.dto.event.request.CreateEventRequest;
import side.onetime.dto.event.request.ModifyUserCreatedEventTitleRequest;
import side.onetime.dto.event.response.*;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.EventErrorStatus;
import side.onetime.exception.status.EventParticipationErrorStatus;
import side.onetime.exception.status.ScheduleErrorStatus;
import side.onetime.repository.EventParticipationRepository;
import side.onetime.repository.EventRepository;
import side.onetime.repository.ScheduleRepository;
import side.onetime.repository.SelectionRepository;
import side.onetime.util.DateUtil;
import side.onetime.util.JwtUtil;
import side.onetime.util.QrUtil;
import side.onetime.util.S3Util;

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
    private final S3Util s3Util;
    private final QrUtil qrUtil;

    /**
     * 비로그인 사용자를 위한 이벤트 생성 메서드.
     * 이 메서드는 비로그인 사용자가 제공한 정보를 기반으로 새로운 이벤트를 생성하고, QR 코드를 생성하여 S3에 업로드합니다.
     *
     * @param createEventRequest 이벤트 생성 요청 데이터 (제목, 시간, 카테고리, 설문 범위 등)
     * @return 생성된 이벤트의 응답 데이터 (이벤트 ID 포함)
     * @throws CustomException QR 코드 생성 실패 또는 잘못된 날짜/요일 포맷일 경우
     */
    @Transactional
    public CreateEventResponse createEventForAnonymousUser(CreateEventRequest createEventRequest) {
        Event event = createEventRequest.toEntity();

        // QR 코드 생성 및 업로드
        String qrFileName = generateAndUploadQrCode(event.getEventId());
        event.addQrFileName(qrFileName);
        eventRepository.save(event);

        if (createEventRequest.category().equals(Category.DATE)) {
            if (!isDateFormat(createEventRequest.ranges().get(0))) {
                throw new CustomException(EventErrorStatus._IS_NOT_DATE_FORMAT);
            }
            createAndSaveDateSchedules(event, createEventRequest.ranges(), createEventRequest.startTime(), createEventRequest.endTime());
        } else {
            if (isDateFormat(createEventRequest.ranges().get(0))) {
                throw new CustomException(EventErrorStatus._IS_NOT_DAY_FORMAT);
            }
            createAndSaveDaySchedules(event, createEventRequest.ranges(), createEventRequest.startTime(), createEventRequest.endTime());
        }

        return CreateEventResponse.of(event);
    }

    /**
     * 인증된 사용자를 위한 이벤트 생성 메서드.
     * 이 메서드는 인증된 사용자가 제공한 정보를 기반으로 새로운 이벤트를 생성하고, QR 코드를 생성하여 S3에 업로드합니다.
     * 생성된 이벤트에 대해 참여 정보(EventParticipation)도 저장됩니다.
     *
     * @param createEventRequest 이벤트 생성 요청 데이터 (제목, 시간, 카테고리, 설문 범위 등)
     * @param authorizationHeader 인증된 사용자의 토큰
     * @return 생성된 이벤트의 응답 데이터 (이벤트 ID 포함)
     * @throws CustomException QR 코드 생성 실패 또는 잘못된 날짜/요일 포맷일 경우
     */
    @Transactional
    public CreateEventResponse createEventForAuthenticatedUser(CreateEventRequest createEventRequest, String authorizationHeader) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Event event = createEventRequest.toEntity();

        // QR 코드 생성 및 업로드
        String qrFileName = generateAndUploadQrCode(event.getEventId());
        event.addQrFileName(qrFileName);
        eventRepository.save(event);
        // 이벤트 참여 여부 저장
        EventParticipation eventParticipation = EventParticipation.builder()
                .user(user)
                .event(event)
                .eventStatus(EventStatus.CREATOR)
                .build();
        eventParticipationRepository.save(eventParticipation);

        if (createEventRequest.category().equals(Category.DATE)) {
            if (!isDateFormat(createEventRequest.ranges().get(0))) {
                throw new CustomException(EventErrorStatus._IS_NOT_DATE_FORMAT);
            }
            createAndSaveDateSchedules(event, createEventRequest.ranges(), createEventRequest.startTime(), createEventRequest.endTime());
        } else {
            if (isDateFormat(createEventRequest.ranges().get(0))) {
                throw new CustomException(EventErrorStatus._IS_NOT_DAY_FORMAT);
            }
            createAndSaveDaySchedules(event, createEventRequest.ranges(), createEventRequest.startTime(), createEventRequest.endTime());
        }

        return CreateEventResponse.of(event);
    }

    /**
     * QR 코드를 생성하고 S3에 업로드하는 메서드.
     * 주어진 이벤트 ID를 기반으로 QR 코드를 생성한 후, S3에 업로드합니다.
     *
     * @param eventId QR 코드를 생성할 이벤트의 ID
     * @return S3에 업로드된 QR 코드 파일 이름
     * @throws CustomException QR 코드 생성 또는 S3 업로드 실패 시 발생
     */
    private String generateAndUploadQrCode(UUID eventId) {
        try {
            MultipartFile qrCodeFile = qrUtil.getQrCodeFile(eventId);
            return s3Util.uploadImage(qrCodeFile);
        } catch (Exception e) {
            throw new CustomException(EventErrorStatus._FAILED_GENERATE_QR_CODE);
        }
    }

    /**
     * 날짜 기반 스케줄을 생성하고 저장하는 메서드.
     * 이벤트의 날짜 범위와 시작/종료 시간을 기반으로 모든 가능한 스케줄을 생성합니다.
     *
     * @param event 이벤트 객체
     * @param ranges 날짜 범위 리스트 (예: ["2024.12.10", "2024.12.11"])
     * @param startTime 시작 시간 (HH:mm 포맷)
     * @param endTime 종료 시간 (HH:mm 포맷)
     */
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

    /**
     * 요일 기반 스케줄을 생성하고 저장하는 메서드.
     * 이벤트의 요일 범위와 시작/종료 시간을 기반으로 모든 가능한 스케줄을 생성합니다.
     *
     * @param event 이벤트 객체
     * @param ranges 요일 범위 리스트 (예: ["MONDAY", "TUESDAY"])
     * @param startTime 시작 시간 (HH:mm 포맷)
     * @param endTime 종료 시간 (HH:mm 포맷)
     */
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

    /**
     * 이벤트 조회 메서드.
     * 특정 이벤트의 세부 정보를 조회하며, 인증된 유저의 경우 추가 정보를 반환합니다.
     *
     * @param eventId 조회할 이벤트의 ID
     * @param authorizationHeader 인증된 유저의 토큰 (선택 사항)
     * @return 조회된 이벤트의 세부 정보
     * @throws CustomException 이벤트 또는 관련 스케줄을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public GetEventResponse getEvent(String eventId, String authorizationHeader) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));

        List<Schedule> schedules = scheduleRepository.findAllByEvent(event)
                .orElseThrow(() -> new CustomException(ScheduleErrorStatus._NOT_FOUND_ALL_SCHEDULES));

        List<String> ranges = event.getCategory().equals(Category.DATE)
                ? DateUtil.getSortedDateRanges(schedules.stream().map(Schedule::getDate).toList(), "yyyy.MM.dd")
                : DateUtil.getSortedDayRanges(schedules.stream().map(Schedule::getDay).toList());

        EventStatus eventStatus = null;
        if (authorizationHeader != null) {
            User user = jwtUtil.getUserFromHeader(authorizationHeader);
            EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event);
            if (eventParticipation != null) {
                eventStatus = eventParticipation.getEventStatus();
            }
        }

        return GetEventResponse.of(event, ranges, eventStatus);
    }

    /**
     * 이벤트 참여자 조회 메서드.
     * 특정 이벤트에 참여한 모든 참여자의 이름 목록(멤버 및 유저)을 반환합니다.
     *
     * @param eventId 참여자를 조회할 이벤트의 ID
     * @return 참여자의 이름 목록
     * @throws CustomException 이벤트를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public GetParticipantsResponse getParticipants(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));

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

        return GetParticipantsResponse.of(members, users);
    }

    /**
     * 가장 많이 되는 시간 조회 메서드.
     * 특정 이벤트에서 참여자 수가 가장 많은 시간대를 계산하여 반환합니다.
     *
     * @param eventId 조회할 이벤트의 ID
     * @return 가능 인원이 많은 시간대 목록
     * @throws CustomException 이벤트를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public List<GetMostPossibleTime> getMostPossibleTime(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));

        // 이벤트에 참여하는 모든 멤버
        List<Member> members = event.getMembers();
        List<String> allMembersName = members.stream()
                .map(Member::getName)
                .toList();

        // 이벤트에 참여하는 모든 유저
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEvent(event);

        GetParticipantsResponse getParticipantsResponse = getParticipants(eventId);
        List<String> participantNames = getParticipantsResponse.names();

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

        List<GetMostPossibleTime> mostPossibleTimes = buildMostPossibleTimes(scheduleToNamesMap, mostPossibleCnt, allParticipants, event.getCategory());

        return DateUtil.sortMostPossibleTimes(mostPossibleTimes, event.getCategory());
    }

    /**
     * 스케줄과 선택된 참여자 이름 매핑 메서드.
     * 각 스케줄에 대해 해당 시간에 참여할 수 있는 멤버와 유저의 이름을 매핑합니다.
     *
     * @param selections 선택 정보 리스트
     * @return 스케줄과 참여자 이름의 매핑 데이터
     */
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

    /**
     * 가장 많이 되는 시간대 리스트 생성 메서드.
     * 참여 가능한 인원이 많은 시간대를 기반으로 시간대 리스트를 생성합니다.
     *
     * @param scheduleToNamesMap 스케줄과 참여자 이름 매핑 데이터
     * @param mostPossibleCnt 가장 많은 참여자 수
     * @param allMembersName 이벤트 참여자의 전체 이름 목록
     * @param category 이벤트의 카테고리 (날짜 또는 요일)
     * @return 가장 많이 되는 시간대 리스트
     */
    private List<GetMostPossibleTime> buildMostPossibleTimes(Map<Schedule, List<String>> scheduleToNamesMap, int mostPossibleCnt, List<String> allMembersName, Category category) {
        List<GetMostPossibleTime> mostPossibleTimes = new ArrayList<>();
        GetMostPossibleTime previousTime = null;

        boolean stopFlag = false;
        for (Map.Entry<Schedule, List<String>> entry : scheduleToNamesMap.entrySet()) {
            Schedule schedule = entry.getKey();
            List<String> curNames = entry.getValue();

            if (curNames.size() == mostPossibleCnt) {
                if (canMergeWithPrevious(previousTime, schedule, curNames, category)) {
                    // 이전 시간대와 병합 가능한 경우
                    previousTime = previousTime.updateEndTime(schedule.getTime());
                    mostPossibleTimes.set(mostPossibleTimes.size() - 1, previousTime); // 종료 시간을 더해 업데이트
                } else {
                    // 새로운 시간대를 추가하는 경우
                    if (mostPossibleTimes.size() == MAX_MOST_POSSIBLE_TIMES_SIZE) {
                        // 6개를 찾았을 시 종료
                        stopFlag = true;
                    }
                    List<String> impossibleNames = allMembersName.stream()
                            .filter(name -> !curNames.contains(name))
                            .toList();

                    // 새로운 시간대를 추가
                    GetMostPossibleTime newTime = createMostPossibleTime(schedule, curNames, impossibleNames, category);
                    mostPossibleTimes.add(newTime);
                    previousTime = newTime;
                }
            }
            if (stopFlag) {
                break;
            }
        }
        return mostPossibleTimes;
    }

    /**
     * 이전 시간대와 병합 가능 여부 확인 메서드.
     * 현재 시간대가 이전 시간대와 연속적이며 참여 가능한 인원이 동일한지 확인합니다.
     *
     * @param previousTime 이전 시간대 정보
     * @param schedule 현재 스케줄
     * @param curNames 현재 시간대의 참여자 이름 목록
     * @param category 이벤트의 카테고리 (날짜 또는 요일)
     * @return 병합 가능 여부
     */
    private boolean canMergeWithPrevious(GetMostPossibleTime previousTime, Schedule schedule, List<String> curNames, Category category) {
        if (previousTime == null) return false;

        boolean isSameTimePoint = category.equals(Category.DAY)
                ? previousTime.timePoint().equals(schedule.getDay())
                : previousTime.timePoint().equals(schedule.getDate());

        return isSameTimePoint
                && previousTime.endTime().equals(schedule.getTime())
                && new HashSet<>(previousTime.possibleNames()).containsAll(curNames);
    }

    /**
     * 새로운 시간대 객체 생성 메서드.
     * 주어진 스케줄 정보와 참여자 데이터를 기반으로 새로운 시간대 객체를 생성합니다.
     *
     * @param schedule 스케줄 정보
     * @param curNames 참여 가능한 이름 목록
     * @param impossibleNames 참여 불가능한 이름 목록
     * @param category 이벤트의 카테고리 (날짜 또는 요일)
     * @return 생성된 시간대 객체
     */
    private GetMostPossibleTime createMostPossibleTime(Schedule schedule, List<String> curNames, List<String> impossibleNames, Category category) {
        return category.equals(Category.DAY)
                ? GetMostPossibleTime.dayOf(schedule, curNames, impossibleNames)
                : GetMostPossibleTime.dateOf(schedule, curNames, impossibleNames);
    }

    /**
     * 날짜 포맷 여부 검증 메서드.
     * 주어진 문자열이 날짜 형식인지 확인합니다.
     *
     * @param range 검증할 문자열
     * @return 날짜 형식 여부
     */
    private boolean isDateFormat(String range) {
        return Character.isDigit(range.charAt(0));
    }

    /**
     * 유저 참여 이벤트 반환 메서드.
     * 인증된 유저가 참여한 모든 이벤트 목록을 조회하며, 각 이벤트에 대한 세부 정보를 반환합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @return 유저가 참여한 이벤트 목록
     */
    @Transactional(readOnly = true)
    public List<GetUserParticipatedEventsResponse> getUserParticipatedEvents(String authorizationHeader) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);

        return eventParticipationRepository.findAllByUser(user).stream()
                .sorted(Comparator.comparing(
                                (EventParticipation eventParticipation) -> eventParticipation.getEvent().getCreatedDate())
                        .reversed()) // 최신순으로 정렬
                .map(eventParticipation -> {
                    Event event = eventParticipation.getEvent();
                    String eventId = String.valueOf(event.getEventId());
                    GetParticipantsResponse getParticipantsResponse = getParticipants(eventId);
                    List<String> participantNames = getParticipantsResponse.names();
                    List<GetMostPossibleTime> mostPossibleTimes = getMostPossibleTime(eventId);
                    return GetUserParticipatedEventsResponse.of(event, eventParticipation, participantNames.size(), mostPossibleTimes);
                })
                .collect(Collectors.toList());
    }

    /**
     * 유저가 생성한 이벤트 삭제 메서드.
     * 인증된 유저가 생성한 특정 이벤트를 삭제합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param eventId 삭제할 이벤트의 ID
     */
    @Transactional
    public void removeUserCreatedEvent(String authorizationHeader, String eventId) {
        EventParticipation eventParticipation = verifyUserIsEventCreator(authorizationHeader, eventId);
        eventRepository.deleteEvent(eventParticipation.getEvent());
    }

    /**
     * 유저가 생성한 이벤트 제목 수정 메서드.
     * 인증된 유저가 생성한 특정 이벤트의 제목을 수정합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param eventId 수정할 이벤트의 ID
     * @param modifyUserCreatedEventTitleRequest 새로운 제목 데이터
     */
    @Transactional
    public void modifyUserCreatedEventTitle(String authorizationHeader, String eventId, ModifyUserCreatedEventTitleRequest modifyUserCreatedEventTitleRequest) {
        EventParticipation eventParticipation = verifyUserIsEventCreator(authorizationHeader, eventId);
        eventParticipation.getEvent().updateTitle(modifyUserCreatedEventTitleRequest.title());
    }

    /**
     * 유저가 이벤트의 생성자인지 검증하는 메서드.
     * 인증된 유저가 특정 이벤트의 생성자인지 확인하고, 생성자인 경우 관련 정보를 반환합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param eventId 확인할 이벤트의 ID
     * @return 이벤트 생성자의 참여 정보
     * @throws CustomException 유저가 생성자가 아니거나 참여 정보를 찾을 수 없는 경우
     */
    private EventParticipation verifyUserIsEventCreator(String authorizationHeader, String eventId) {
        User user = jwtUtil.getUserFromHeader(authorizationHeader);
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));

        EventParticipation eventParticipation = eventParticipationRepository.findByUserAndEvent(user, event);
        if (eventParticipation == null) {
            throw new CustomException(EventParticipationErrorStatus._NOT_FOUND_EVENT_PARTICIPATION);
        }
        if (!EventStatus.CREATOR.equals(eventParticipation.getEventStatus())) {
            throw new CustomException(EventParticipationErrorStatus._IS_NOT_USERS_CREATED_EVENT_PARTICIPATION);
        }

        return eventParticipation;
    }

    /**
     * 이벤트 QR Code 조회 메서드.
     * 특정 이벤트의 QR 코드 이미지를 S3에서 가져와 URL을 반환합니다.
     *
     * @param eventId QR 코드를 조회할 이벤트의 ID
     * @return QR 코드 이미지 URL
     * @throws CustomException 이벤트 또는 QR 코드를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public GetEventQrCodeResponse getEventQrCode(String eventId) {
        Event event = eventRepository.findByEventId(UUID.fromString(eventId))
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));
        if (event.getQrFileName() == null) {
            throw new CustomException(EventErrorStatus._NOT_FOUND_EVENT_QR_CODE);
        }

        String qrCodeImgUrl = s3Util.getPublicUrl(event.getQrFileName());
        return GetEventQrCodeResponse.from(qrCodeImgUrl);
    }
}
