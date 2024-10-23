package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.fixedEvent.request.CreateFixedEventRequest;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.FixedEventService;

@RestController
@RequestMapping("/api/v1/fixed-events")
@RequiredArgsConstructor
public class FixedEventController {
    private final FixedEventService fixedEventService;

    // 고정 이벤트 생성 및 고정 스케줄 등록 API
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createFixedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody CreateFixedEventRequest createFixedEventRequest) {

        fixedEventService.createFixedEvent(authorizationHeader, createFixedEventRequest);

        return ApiResponse.onSuccess(SuccessStatus._CREATED_FIXED_SCHEDULE);
    }
}
