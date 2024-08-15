package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import side.onetime.domain.Event;
import side.onetime.domain.Member;
import side.onetime.dto.MemberDto;
import side.onetime.exception.EventErrorResult;
import side.onetime.exception.EventException;
import side.onetime.exception.MemberErrorResult;
import side.onetime.exception.MemberException;
import side.onetime.repository.EventRepository;
import side.onetime.repository.MemberRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    // 멤버 로그인 메서드
    @Transactional
    public MemberDto.LoginMemberResponse loginMember(MemberDto.LoginMemberRequest loginMemberRequest) {
        UUID eventId = UUID.fromString(loginMemberRequest.getEventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));

        // 이미 있는 유저인 경우 반환, 없을 경우 생성
        Member member;
        if (memberRepository.existsByEventAndNameAndPin(event, loginMemberRequest.getName(), loginMemberRequest.getPin())) {
            member = memberRepository.findByEventAndNameAndPin(event, loginMemberRequest.getName(), loginMemberRequest.getPin())
                    .orElseThrow(() -> new MemberException(MemberErrorResult._NOT_FOUND_MEMBER));
        } else {
            member = loginMemberRequest.to(event);
            memberRepository.save(member);
        }
        return MemberDto.LoginMemberResponse.of(member);
    }

    // 멤버 이름 중복 체크 메서드
    public MemberDto.IsDuplicateResponse isDuplicate(MemberDto.IsDuplicateRequest isDuplicateRequest) {
        UUID eventId = UUID.fromString(isDuplicateRequest.getEventId());
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new EventException(EventErrorResult._NOT_FOUND_EVENT));
        return MemberDto.IsDuplicateResponse.of(!memberRepository.existsByEventAndName(event, isDuplicateRequest.getName()));
    }
}