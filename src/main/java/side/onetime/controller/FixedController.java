package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import side.onetime.auth.dto.CustomUserDetails;
import side.onetime.dto.fixed.request.UpdateFixedScheduleRequest;
import side.onetime.dto.fixed.response.GetFixedScheduleResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.FixedScheduleService;

@RestController
@RequestMapping("/api/v1/fixed-schedules")
@RequiredArgsConstructor
public class FixedController {

    private final FixedScheduleService fixedScheduleService;

    /**
     * 유저의 고정 스케줄 조회 API.
     *
     * 인증된 유저의 현재 등록된 고정 스케줄을 조회합니다.
     *
     * @param customUserDetails 인증된 사용자 정보
     * @return 유저의 고정 스케줄 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<GetFixedScheduleResponse>> getUserFixedSchedule(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        GetFixedScheduleResponse response = fixedScheduleService.getUserFixedSchedule(customUserDetails.user());
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_FIXED_SCHEDULE, response);
    }

    /**
     * 유저의 고정 스케줄 수정 API.
     *
     * 기존에 등록된 고정 스케줄을 삭제하고, 새로운 스케줄을 저장합니다.
     *
     * @param request 새로운 고정 스케줄 목록
     * @param customUserDetails 인증된 사용자 정보
     * @return 성공 상태 응답 객체
     */
    @PutMapping
    public ResponseEntity<ApiResponse<SuccessStatus>> updateUserFixedSchedules(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdateFixedScheduleRequest request) {

        fixedScheduleService.updateUserFixedSchedules(customUserDetails.user(), request);
        return ApiResponse.onSuccess(SuccessStatus._UPDATE_USER_FIXED_SCHEDULE);
    }
}
