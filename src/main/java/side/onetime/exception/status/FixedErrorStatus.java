package side.onetime.exception.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import side.onetime.global.common.code.BaseErrorCode;
import side.onetime.global.common.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum FixedErrorStatus implements BaseErrorCode {
    _NOT_FOUND_FIXED_SCHEDULES(HttpStatus.NOT_FOUND, "FIXED-001", "고정 스케줄 목록을 가져오는 데 실패했습니다."),
    _NOT_FOUND_FIXED_EVENTS(HttpStatus.NOT_FOUND, "FIXED-002", "고정 이벤트 목록을 가져오는 데 실패했습니다."),
    _NOT_FOUND_FIXED_EVENT(HttpStatus.NOT_FOUND, "FIXED-003", "특정 고정 이벤트를 가져오는 데 실패했습니다."),
    _NOT_FOUND_FIXED_SELECTIONS(HttpStatus.NOT_FOUND, "FIXED-004", "고정 스케줄 선택 목록을 가져오는 데 실패했습니다."),
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