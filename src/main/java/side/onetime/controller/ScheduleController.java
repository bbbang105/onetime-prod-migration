package side.onetime.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import side.onetime.dto.ScheduleDto;
import side.onetime.global.common.ApiResponse;
import side.onetime.global.common.constant.SuccessStatus;
import side.onetime.service.ScheduleService;

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
}
