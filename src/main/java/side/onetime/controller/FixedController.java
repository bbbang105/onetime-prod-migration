package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.fixed.request.CreateFixedEventRequest;
import side.onetime.dto.fixed.response.FixedEventResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.FixedEventService;
import side.onetime.service.FixedScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fixed-schedules")
@RequiredArgsConstructor
public class FixedController {
    private final FixedEventService fixedEventService;
    private final FixedScheduleService fixedScheduleService;

    // 고정 이벤트 생성 및 고정 스케줄 등록 API
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createFixedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody CreateFixedEventRequest createFixedEventRequest) {

        fixedEventService.createFixedEvent(authorizationHeader, createFixedEventRequest);

        return ApiResponse.onSuccess(SuccessStatus._CREATED_FIXED_SCHEDULE);
    }

    // 전체 고정 스케줄 조회 API
    @GetMapping
    public ResponseEntity<ApiResponse<List<FixedEventResponse>>> getAllFixedSchedules(
            @RequestHeader("Authorization") String authorizationHeader) {

        List<FixedEventResponse> fixedEventResponses = fixedScheduleService.getAllFixedSchedules(authorizationHeader);

        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_FIXED_SCHEDULES, fixedEventResponses);
    }
}
