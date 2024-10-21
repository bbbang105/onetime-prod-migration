package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.schedule.request.CreateDateScheduleRequest;
import side.onetime.dto.schedule.request.CreateDayScheduleRequest;
import side.onetime.dto.schedule.request.GetFilteredSchedulesRequest;
import side.onetime.dto.schedule.response.PerDateSchedulesResponse;
import side.onetime.dto.schedule.response.PerDaySchedulesResponse;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.status.SuccessStatus;
import side.onetime.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 요일 스케줄 등록 API
    @PostMapping("/day")
    public ResponseEntity<ApiResponse<SuccessStatus>> createDaySchedules(
            @RequestBody CreateDayScheduleRequest createDayScheduleRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader != null) {
            scheduleService.createDaySchedulesForAuthenticatedUser(createDayScheduleRequest, authorizationHeader);
        } else {
            scheduleService.createDaySchedulesForAnonymousUser(createDayScheduleRequest);
        }
        return ApiResponse.onSuccess(SuccessStatus._CREATED_DAY_SCHEDULES);
    }

    // 날짜 스케줄 등록 API
    @PostMapping("/date")
    public ResponseEntity<ApiResponse<SuccessStatus>> createDateSchedules(
            @RequestBody CreateDateScheduleRequest createDateScheduleRequest,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader != null) {
            scheduleService.createDateSchedulesForAuthenticatedUser(createDateScheduleRequest, authorizationHeader);
        } else {
            scheduleService.createDateSchedulesForAnonymousUser(createDateScheduleRequest);
        }
        return ApiResponse.onSuccess(SuccessStatus._CREATED_DATE_SCHEDULES);
    }

    // 전체 요일 스케줄 조회 API
    @GetMapping("/day/{event_id}")
    public ResponseEntity<ApiResponse<List<PerDaySchedulesResponse>>> getAllDaySchedules(
            @PathVariable("event_id") String eventId) {

        List<PerDaySchedulesResponse> perDaySchedulesResponses = scheduleService.getAllDaySchedules(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_DAY_SCHEDULES, perDaySchedulesResponses);
    }

    // 개인 요일 스케줄 조회 API (비로그인)
    @GetMapping("/day/{event_id}/{member_id}")
    public ResponseEntity<ApiResponse<PerDaySchedulesResponse>> getMemberDaySchedules(
            @PathVariable("event_id") String eventId,
            @PathVariable("member_id") String memberId) {

        PerDaySchedulesResponse perDaySchedulesResponse = scheduleService.getMemberDaySchedules(eventId, memberId);
        return ApiResponse.onSuccess(SuccessStatus._GET_MEMBER_DAY_SCHEDULES, perDaySchedulesResponse);
    }

    // 개인 요일 스케줄 조회 API (로그인)
    @GetMapping("/day/{event_id}/user")
    public ResponseEntity<ApiResponse<PerDaySchedulesResponse>> getUserDaySchedules(
            @PathVariable("event_id") String eventId,
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        PerDaySchedulesResponse perDaySchedulesResponse = scheduleService.getUserDaySchedules(eventId, authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_DAY_SCHEDULES, perDaySchedulesResponse);
    }

    // 멤버 필터링 요일 스케줄 조회 API
    @GetMapping("/day/action-filtering")
    public ResponseEntity<ApiResponse<List<PerDaySchedulesResponse>>> getFilteredDaySchedules(
            @RequestBody GetFilteredSchedulesRequest getFilteredSchedulesRequest) {

        List<PerDaySchedulesResponse> perDaySchedulesResponses = scheduleService.getFilteredDaySchedules(getFilteredSchedulesRequest);
        return ApiResponse.onSuccess(SuccessStatus._GET_FILTERED_DAY_SCHEDULES, perDaySchedulesResponses);
    }

    // 전체 날짜 스케줄 조회 API
    @GetMapping("/date/{event_id}")
    public ResponseEntity<ApiResponse<List<PerDateSchedulesResponse>>> getAllDateSchedules(
            @PathVariable("event_id") String eventId) {

        List<PerDateSchedulesResponse> perDateSchedulesResponses = scheduleService.getAllDateSchedules(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_DATE_SCHEDULES, perDateSchedulesResponses);
    }

    // 개인 날짜 스케줄 조회 API (비로그인)
    @GetMapping("/date/{event_id}/{member_id}")
    public ResponseEntity<ApiResponse<PerDateSchedulesResponse>> getMemberDateSchedules(
            @PathVariable("event_id") String eventId,
            @PathVariable("member_id") String memberId) {

        PerDateSchedulesResponse perDateSchedulesResponse = scheduleService.getMemberDateSchedules(eventId, memberId);
        return ApiResponse.onSuccess(SuccessStatus._GET_MEMBER_DATE_SCHEDULES, perDateSchedulesResponse);
    }

    // 개인 날짜 스케줄 조회 API (로그인)
    @GetMapping("/date/{event_id}/user")
    public ResponseEntity<ApiResponse<PerDateSchedulesResponse>> getUserDateSchedules(
            @PathVariable("event_id") String eventId,
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        PerDateSchedulesResponse perDateSchedulesResponse = scheduleService.getUserDateSchedules(eventId, authorizationHeader);
        return ApiResponse.onSuccess(SuccessStatus._GET_USER_DATE_SCHEDULES, perDateSchedulesResponse);
    }

    // 멤버 필터링 날짜 스케줄 조회 API
    @GetMapping("/date/action-filtering")
    public ResponseEntity<ApiResponse<List<PerDateSchedulesResponse>>> getFilteredDateSchedules(
            @RequestBody GetFilteredSchedulesRequest getFilteredSchedulesRequest) {

        List<PerDateSchedulesResponse> perDateSchedulesResponses = scheduleService.getFilteredDateSchedules(getFilteredSchedulesRequest);
        return ApiResponse.onSuccess(SuccessStatus._GET_FILTERED_DATE_SCHEDULES, perDateSchedulesResponses);
    }
}
