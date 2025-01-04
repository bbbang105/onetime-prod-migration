package side.onetime.exception.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorStatus implements BaseErrorCode {
    _NOT_FOUND_ALL_SCHEDULES(HttpStatus.NOT_FOUND, "SCHEDULE-001", "전체 스케줄을 가져오는 데 실패했습니다."),
    _NOT_FOUND_DAY_SCHEDULES(HttpStatus.NOT_FOUND, "SCHEDULE-002", "요일 스케줄을 가져오는 데 실패했습니다."),
    _NOT_FOUND_DATE_SCHEDULES(HttpStatus.NOT_FOUND, "SCHEDULE-003", "날짜 스케줄을 가져오는 데 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
