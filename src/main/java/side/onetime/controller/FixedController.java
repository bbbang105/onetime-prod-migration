package side.onetime.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.fixed.request.CreateFixedEventRequest;
import side.onetime.dto.fixed.request.ModifyFixedEventRequest;
import side.onetime.dto.fixed.response.FixedEventByDayResponse;
import side.onetime.dto.fixed.response.FixedEventDetailResponse;
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

    /**
     * 고정 이벤트 생성 및 고정 스케줄 등록 API
     *
     * 이 API는 새로운 고정 이벤트를 생성하고 관련된 고정 스케줄을 등록합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param createFixedEventRequest 생성할 고정 이벤트에 대한 요청 데이터 (제목, 스케줄 목록 등)
     * @return 생성 성공 여부를 나타내는 메시지
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createFixedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody CreateFixedEventRequest createFixedEventRequest) {

        fixedEventService.createFixedEvent(authorizationHeader, createFixedEventRequest);
        return ApiResponse.onSuccess(SuccessStatus._CREATED_FIXED_SCHEDULE);
    }

    /**
     * 전체 고정 스케줄 조회 API
     *
     * 이 API는 유저가 등록한 모든 고정 스케줄을 조회합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @return 유저가 등록한 모든 고정 스케줄 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FixedEventResponse>>> getAllFixedSchedules(
            @RequestHeader("Authorization") String authorizationHeader) {

        List<FixedEventResponse> fixedEventResponses = fixedScheduleService.getAllFixedSchedules(authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_FIXED_SCHEDULES, fixedEventResponses);
    }

    /**
     * 특정 고정 스케줄 상세 조회 API
     *
     * 이 API는 특정 ID에 해당하는 고정 스케줄의 상세 정보를 조회합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param fixedEventId 조회할 고정 스케줄의 ID
     * @return 조회된 고정 스케줄의 세부 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FixedEventDetailResponse>> getFixedScheduleDetail(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("id") Long fixedEventId) {

        FixedEventDetailResponse fixedEventDetailResponse = fixedScheduleService.getFixedScheduleDetail(authorizationHeader, fixedEventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_FIXED_SCHEDULE_DETAIL, fixedEventDetailResponse);
    }

    /**
     * 고정 이벤트 또는 스케줄 수정 API
     *
     * 이 API는 특정 고정 이벤트의 제목과 스케줄을 수정할 수 있습니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param fixedEventId 수정할 고정 이벤트의 ID
     * @param modifyFixedEventRequest 수정할 고정 이벤트의 제목 및 스케줄
     * @return 수정 성공 여부를 나타내는 메시지
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> modifyFixedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("id") Long fixedEventId,
            @RequestBody ModifyFixedEventRequest modifyFixedEventRequest) {

        if (modifyFixedEventRequest.title() != null) {
            fixedEventService.modifyFixedEvent(authorizationHeader, fixedEventId, modifyFixedEventRequest);
        }
        if (modifyFixedEventRequest.schedules() != null) {
            fixedScheduleService.modifyFixedSchedule(authorizationHeader, fixedEventId, modifyFixedEventRequest);
        }

        return ApiResponse.onSuccess(SuccessStatus._MODIFY_FIXED_SCHEDULE);
    }

    /**
     * 고정 이벤트 & 스케줄 삭제 API
     *
     * 이 API는 특정 ID에 해당하는 고정 이벤트와 관련된 스케줄을 삭제합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param fixedEventId 삭제할 고정 이벤트의 ID
     * @return 삭제 성공 여부를 나타내는 메시지
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> removeFixedEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("id") Long fixedEventId) {

        fixedEventService.removeFixedEvent(authorizationHeader, fixedEventId);
        return ApiResponse.onSuccess(SuccessStatus._REMOVE_FIXED_SCHEDULE);
    }

    /**
     * 요일별 고정 이벤트 조회 API
     *
     * 이 API는 특정 요일에 해당하는 고정 이벤트 목록을 조회합니다.
     *
     * @param authorizationHeader 인증된 유저의 토큰
     * @param day 조회할 요일 (예: 월, 화 등)
     * @return 조회된 요일의 고정 이벤트 목록
     */
    @GetMapping("by-day/{day}")
    public ResponseEntity<ApiResponse<List<FixedEventByDayResponse>>> getFixedEventByDay(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("day") String day) {

        List<FixedEventByDayResponse> response = fixedEventService.getFixedEventByDay(authorizationHeader, day);
        return ApiResponse.onSuccess(SuccessStatus._GET_FIXED_EVENT_BY_DAY, response);
    }
}