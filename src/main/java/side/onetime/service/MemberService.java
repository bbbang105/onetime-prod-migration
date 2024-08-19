package side.onetime.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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