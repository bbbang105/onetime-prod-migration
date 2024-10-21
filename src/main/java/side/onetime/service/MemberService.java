package side.onetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.domain.Schedule;
import side.onetime.domain.Selection;
import side.onetime.domain.enums.Category;
import side.onetime.dto.member.request.IsDuplicateRequest;
import side.onetime.dto.member.request.LoginMemberRequest;
import side.onetime.dto.member.request.RegisterMemberRequest;
import side.onetime.dto.member.response.IsDuplicateResponse;
import side.onetime.dto.member.response.LoginMemberResponse;
import side.onetime.dto.member.response.RegisterMemberResponse;
import side.onetime.dto.member.response.ScheduleResponse;
import side.onetime.exception.CustomException;
import side.onetime.exception.status.EventErrorStatus;
import side.onetime.exception.status.MemberErrorStatus;
import side.onetime.exception.status.ScheduleErrorStatus;
import side.onetime.repository.EventRepository;
import side.onetime.repository.MemberRepository;
import side.onetime.repository.ScheduleRepository;
import side.onetime.repository.SelectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final SelectionRepository selectionRepository;
    private final ScheduleRepository scheduleRepository;

    // 멤버 등록 메서드
    @Transactional
    public RegisterMemberResponse registerMember(RegisterMemberRequest registerMemberRequest) {
        UUID eventId = UUID.fromString(registerMemberRequest.eventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));

        if (memberRepository.existsByEventAndName(event, registerMemberRequest.name())) {
            throw new CustomException(MemberErrorStatus._IS_ALREADY_REGISTERED);
        }

        Member member = registerMemberRequest.toEntity(event);
        memberRepository.save(member);

        List<Selection> selections;
        if (event.getCategory().equals(Category.DAY)) {
            selections = createMembersDaySelections(event, member, registerMemberRequest);
        } else {
            selections = createMembersDateSelections(event, member, registerMemberRequest);
        }
        selectionRepository.saveAll(selections);

        return RegisterMemberResponse.of(member, event);
    }

    // 멤버 요일 선택 목록을 만드는 메서드
    private List<Selection> createMembersDaySelections(Event event, Member member, RegisterMemberRequest registerMemberRequest) {
        List<ScheduleResponse> schedules = registerMemberRequest.schedules();
        List<Selection> selections = new ArrayList<>();
        for (ScheduleResponse schedule : schedules) {
            String day = schedule.timePoint();
            List<String> times = schedule.times();
            List<Schedule> selectedSchedules = scheduleRepository.findAllByEventAndDay(event, day)
                    .orElseThrow(() -> new CustomException(ScheduleErrorStatus._NOT_FOUND_DAY_SCHEDULES));

            for (Schedule selectedSchedule : selectedSchedules) {
                if (times.contains(selectedSchedule.getTime())) {
                    selections.add(Selection.builder()
                            .member(member)
                            .schedule(selectedSchedule)
                            .build());
                }
            }
        }
        return selections;
    }

    // 멤버 날짜 선택 목록을 만드는 메서드
    private List<Selection> createMembersDateSelections(Event event, Member member, RegisterMemberRequest registerMemberRequest) {
        List<ScheduleResponse> schedules = registerMemberRequest.schedules();
        List<Selection> selections = new ArrayList<>();
        for (ScheduleResponse schedule : schedules) {
            String date = schedule.timePoint();
            List<String> times = schedule.times();
            List<Schedule> selectedSchedules = scheduleRepository.findAllByEventAndDate(event, date)
                    .orElseThrow(() -> new CustomException(ScheduleErrorStatus._NOT_FOUND_DATE_SCHEDULES));

            for (Schedule selectedSchedule : selectedSchedules) {
                if (times.contains(selectedSchedule.getTime())) {
                    selections.add(Selection.builder()
                            .member(member)
                            .schedule(selectedSchedule)
                            .build());
                }
            }
        }
        return selections;
    }

    // 멤버 로그인 메서드
    @Transactional(readOnly = true)
    public LoginMemberResponse loginMember(LoginMemberRequest loginMemberRequest) {
        UUID eventId = UUID.fromString(loginMemberRequest.eventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));

        Member member = memberRepository.findByEventAndNameAndPin(event, loginMemberRequest.name(), loginMemberRequest.pin())
                .orElseThrow(() -> new CustomException(MemberErrorStatus._NOT_FOUND_MEMBER));

        return LoginMemberResponse.of(member, event);
    }

    // 멤버 이름 중복 체크 메서드
    @Transactional(readOnly = true)
    public IsDuplicateResponse isDuplicate(IsDuplicateRequest isDuplicateRequest) {
        UUID eventId = UUID.fromString(isDuplicateRequest.eventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomException(EventErrorStatus._NOT_FOUND_EVENT));
        return IsDuplicateResponse.of(!memberRepository.existsByEventAndName(event, isDuplicateRequest.name()));
    }
}