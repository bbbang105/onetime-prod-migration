package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.MemberDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.constant.SuccessStatus;
import side.onetime.service.MemberService;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 멤버 로그인 API
    @PostMapping("/action-login")
    public ResponseEntity<ApiResponse<MemberDto.LoginMemberResponse>> loginMember(
            @RequestBody MemberDto.LoginMemberRequest loginMemberRequest) {

        MemberDto.LoginMemberResponse loginMemberResponse = memberService.loginMember(loginMemberRequest);
        return ApiResponse.onSuccess(SuccessStatus._LOGIN_MEMBER, loginMemberResponse);
    }

    // 이름 중복 확인 API
    @PostMapping("/name/action-check")
    public ResponseEntity<ApiResponse<MemberDto.IsDuplicateResponse>> isDuplicate(
            @RequestBody MemberDto.IsDuplicateRequest isDuplicateRequest) {

        MemberDto.IsDuplicateResponse isDuplicateResponse = memberService.isDuplicate(isDuplicateRequest);
        return ApiResponse.onSuccess(SuccessStatus._IS_POSSIBLE_NAME, isDuplicateResponse);
    }
}
