package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.member.request.IsDuplicateRequest;
import side.onetime.dto.member.request.LoginMemberRequest;
import side.onetime.dto.member.request.RegisterMemberRequest;
import side.onetime.dto.member.response.IsDuplicateResponse;
import side.onetime.dto.member.response.LoginMemberResponse;
import side.onetime.dto.member.response.RegisterMemberResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.MemberService;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 멤버 등록 API
    @PostMapping("/action-register")
    public ResponseEntity<ApiResponse<RegisterMemberResponse>> registerMember(
            @Valid @RequestBody RegisterMemberRequest registerMemberRequest) {

        RegisterMemberResponse registerMemberResponse = memberService.registerMember(registerMemberRequest);
        return ApiResponse.onSuccess(SuccessStatus._REGISTER_MEMBER, registerMemberResponse);
    }

    // 멤버 로그인 API
    @PostMapping("/action-login")
    public ResponseEntity<ApiResponse<LoginMemberResponse>> loginMember(
            @Valid @RequestBody LoginMemberRequest loginMemberRequest) {

        LoginMemberResponse loginMemberResponse = memberService.loginMember(loginMemberRequest);
        return ApiResponse.onSuccess(SuccessStatus._LOGIN_MEMBER, loginMemberResponse);
    }

    // 이름 중복 확인 API
    @PostMapping("/name/action-check")
    public ResponseEntity<ApiResponse<IsDuplicateResponse>> isDuplicate(
            @Valid @RequestBody IsDuplicateRequest isDuplicateRequest) {

        IsDuplicateResponse isDuplicateResponse = memberService.isDuplicate(isDuplicateRequest);
        return ApiResponse.onSuccess(SuccessStatus._IS_POSSIBLE_NAME, isDuplicateResponse);
    }
}
