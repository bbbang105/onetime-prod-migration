package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.domain.Schedule;
import side.onetime.domain.Selection;
import side.onetime.dto.MemberDto;
import side.onetime.exception.*;
import side.onetime.global.common.constant.Category;
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
    public MemberDto.RegisterMemberResponse registerMember(MemberDto.RegisterMemberRequest registerMemberRequest) {
        UUID eventId = UUID.fromString(registerMemberRequest.getEventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        if (memberRepository.existsByEventAndName(event, registerMemberRequest.getName())) {
            throw new MemberException(MemberErrorResult._IS_ALREADY_REGISTERED);
        }

        Member member = registerMemberRequest.to(event);
        memberRepository.save(member);

        List<Selection> selections;
        if (event.getCategory().equals(Category.DAY)) {
            selections = createMembersDaySelections(event, member, registerMemberRequest);
        } else {
            selections = createMembersDateSelections(event, member, registerMemberRequest);
        }
        selectionRepository.saveAll(selections);

        return MemberDto.RegisterMemberResponse.of(member, event);
    }

    private List<Selection> createMembersDaySelections(Event event, Member member, MemberDto.RegisterMemberRequest registerMemberRequest) {
        List<MemberDto.Schedule> schedules = registerMemberRequest.getSchedules();
        List<Selection> selections = new ArrayList<>();
        for (MemberDto.Schedule schedule : schedules) {
            String day = schedule.getTimePoint();
            List<String> times = schedule.getTimes();
            List<Schedule> selectedSchedules = scheduleRepository.findAllByEventAndDay(event, day)
                    .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_DAY_SCHEDULES));

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

    private List<Selection> createMembersDateSelections(Event event, Member member, MemberDto.RegisterMemberRequest registerMemberRequest) {
        List<MemberDto.Schedule> schedules = registerMemberRequest.getSchedules();
        List<Selection> selections = new ArrayList<>();
        for (MemberDto.Schedule schedule : schedules) {
            String date = schedule.getTimePoint();
            List<String> times = schedule.getTimes();
            List<Schedule> selectedSchedules = scheduleRepository.findAllByEventAndDate(event, date)
                    .orElseThrow(() -> new ScheduleException(ScheduleErrorResult._NOT_FOUND_DATE_SCHEDULES));

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
    public MemberDto.LoginMemberResponse loginMember(MemberDto.LoginMemberRequest loginMemberRequest) {
        UUID eventId = UUID.fromString(loginMemberRequest.getEventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        Member member = memberRepository.findByEventAndNameAndPin(event, loginMemberRequest.getName(), loginMemberRequest.getPin())
                .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));

        return MemberDto.LoginMemberResponse.of(member, event);
    }

    // 멤버 이름 중복 체크 메서드
    public MemberDto.IsDuplicateResponse isDuplicate(MemberDto.IsDuplicateRequest isDuplicateRequest) {
        UUID eventId = UUID.fromString(isDuplicateRequest.getEventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        return MemberDto.IsDuplicateResponse.of(!memberRepository.existsByEventAndName(event, isDuplicateRequest.getName()));
    }
}