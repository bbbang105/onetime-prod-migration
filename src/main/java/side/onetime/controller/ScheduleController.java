package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import side.onetime.dto.ScheduleDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.constant.SuccessStatus;
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
            @RequestBody ScheduleDto.CreateDayScheduleRequest createDayScheduleRequest) {

        scheduleService.createDaySchedules(createDayScheduleRequest);
        return ApiResponse.onSuccess(SuccessStatus._CREATED_DAY_SCHEDULES);
    }

    // 날짜 스케줄 등록 API
    @PostMapping("/date")
    public ResponseEntity<ApiResponse<SuccessStatus>> createDateSchedules(
            @RequestBody ScheduleDto.CreateDateScheduleRequest createDateScheduleRequest) {

        scheduleService.createDateSchedules(createDateScheduleRequest);
        return ApiResponse.onSuccess(SuccessStatus._CREATED_DATE_SCHEDULES);
    }

    // 전체 요일 스케줄 조회 API
    @GetMapping("/day/{event_id}")
    public ResponseEntity<ApiResponse<List<ScheduleDto.PerDaySchedulesResponse>>> getAllDaySchedules(
            @PathVariable("event_id") String eventId) {

        List<ScheduleDto.PerDaySchedulesResponse> perDaySchedulesResponses = scheduleService.getAllDaySchedules(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_DAY_SCHEDULES, perDaySchedulesResponses);
    }

    // 개인 요일 스케줄 조회 API
    @GetMapping("/day/{event_id}/{member_id}")
    public ResponseEntity<ApiResponse<ScheduleDto.PerDaySchedulesResponse>> getMemberDaySchedules(
            @PathVariable("event_id") String eventId,
            @PathVariable("member_id") String memberId) {

        ScheduleDto.PerDaySchedulesResponse perDaySchedulesResponse = scheduleService.getMemberDaySchedules(eventId, memberId);
        return ApiResponse.onSuccess(SuccessStatus._GET_MEMBER_DAY_SCHEDULES, perDaySchedulesResponse);
    }

    // 전체 날짜 스케줄 조회 API
    @GetMapping("/date/{event_id}")
    public ResponseEntity<ApiResponse<List<ScheduleDto.PerDateSchedulesResponse>>> getAllDateSchedules(
            @PathVariable("event_id") String eventId) {

        List<ScheduleDto.PerDateSchedulesResponse> perDateSchedulesResponses = scheduleService.getAllDateSchedules(eventId);
        return ApiResponse.onSuccess(SuccessStatus._GET_ALL_DATE_SCHEDULES, perDateSchedulesResponses);
    }
}
